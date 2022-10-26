package com.happymall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.common.to.OrderTo;
import com.common.utils.R;
import com.common.vo.MemberRespVo;
import com.happymall.order.constant.OrderConstant;
import com.happymall.order.entity.OrderItemEntity;
import com.happymall.order.entity.PaymentInfoEntity;
import com.happymall.order.enume.OrderStatusEnum;
import com.happymall.order.feign.CartFeignService;
import com.happymall.order.feign.MemberFeignService;
import com.happymall.order.feign.ProductFeignService;
import com.happymall.order.feign.WmsFeignService;
import com.happymall.order.interceptor.LoginUserInterceptor;
import com.happymall.order.service.OrderItemService;
import com.happymall.order.service.PaymentInfoService;
import com.happymall.order.to.OrderCreateTo;
import com.happymall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.happymall.order.dao.OrderDao;
import com.happymall.order.entity.OrderEntity;
import com.happymall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> orderThreadLocal = new ThreadLocal<>();

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmVo = new OrderConfirmVo();

        MemberRespVo user = LoginUserInterceptor.loginUser.get();

        // RequestContextHolder use threadlocal, different thread has different data
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();


        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            // 1. Get user's addresses
            RequestContextHolder.setRequestAttributes(attributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(user.getId());
            confirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> getCartItemsFuture = CompletableFuture.runAsync(() -> {
            // 2. Get items in cart
            RequestContextHolder.setRequestAttributes(attributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> idList = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());

            R r = wmsFeignService.getSkusHasStock(idList);
            List<SkuStockVo> data = r.getData(new TypeReference<List<SkuStockVo>>() {});
            if (data != null){
                Map<Long, Boolean> map =
                        data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        });


        // 3. Get user's gift card balance
        confirmVo.setIntegration(user.getIntegration());

        // 4. Other data calculate automatically

        // TODO order token (prevent place order repeatedly)
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+user.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        CompletableFuture.allOf(getAddressFuture, getCartItemsFuture).get();

        return confirmVo;
    }

    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {

        // Thread local share order info
        orderThreadLocal.set(vo);

        // Create order response object return to page
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(0);

        // Get user session info from interceptor
        MemberRespVo user = LoginUserInterceptor.loginUser.get();

        /**
         * Valid token
         * token validation and deletion must be atomic
         * Use lua script can guarantee atomic
         */

        // If redis token == order token, valid successful and delete token in redis
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        Long result = redisTemplate.execute(
                new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + user.getId()),
                orderToken);

        if (result == 0L){
            // Validate fail
            responseVo.setCode(1);
            return responseVo;
        } else {
            // Validate successful
            // Create order, order items
            OrderCreateTo order = createOrder();

            // Valid price
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
                // Valid price successful and save order and order items into database
                saveOrder(order);

                // Lock stock, roll back order if have exception
                // order num, sku id, num,
                WareSkuLockVo lockVo = new WareSkuLockVo();

                lockVo.setOrderSn(order.getOrder().getOrderSn());

                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());

                lockVo.setLocks(locks);

                // Use MQ
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0){
                    // Lock successfully
                    responseVo.setOrder(order.getOrder());

                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());

                    return  responseVo;
                } else {
                    responseVo.setCode(3);
                    return responseVo;
                }

            } else {
                responseVo.setCode(2);
                return responseVo;
            }

        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity order_sn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return order_sn;
    }

    @Override
    public void closeOrder(OrderEntity order) {
        OrderEntity orderEntity = this.getById(order.getId());

        // If after 30 mins, the payment still pending, close the order
        if (orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()){

            OrderEntity update = new OrderEntity();
            update.setId(order.getId());

            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);

            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity,orderTo);
            // Send stock release message again
            try {
                rabbitTemplate.convertAndSend("order-event-exchange",
                        "order.release.other", orderTo);
            } catch (Exception e){
                
            }

        }

    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity order = this.getOrderByOrderSn(orderSn);

        BigDecimal amount = order.getPayAmount().setScale(2);
        payVo.setTotal_amount(amount.toString());
        payVo.setOut_trade_no(orderSn);
        payVo.setSubject("happymall");

        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {

        MemberRespVo user = LoginUserInterceptor.loginUser.get();

        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",user.getId()).orderByDesc("id")
        );

        List<OrderEntity> collect = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> orderItems = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(orderItems);
            return order;
        }).collect(Collectors.toList());

        page.setRecords(collect);

        return new PageUtils(page);

    }

    /**
     * Handle alipay return pay result
     * @param vo
     * @return
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {

        // Save payment info
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setAlipayTradeNo(vo.getTrade_no());
        paymentInfo.setOrderSn(vo.getOut_trade_no());
        paymentInfo.setPaymentStatus(vo.getTrade_status());
        paymentInfo.setCallbackTime(vo.getNotify_time());

        paymentInfoService.save(paymentInfo);

        if(vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")){
            // Payment success
            String tradeNo = vo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(tradeNo, OrderStatusEnum.PAYED.getCode());
        }

        return "success";
    }


    /**
     * Save order info
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {

        // Save order entity into database
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);

        // Save order items into database
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }

    /**
     * Build order entity, order item entity, compute price
     * @return
     */
    private OrderCreateTo createOrder(){
        OrderCreateTo orderCreateTo = new OrderCreateTo();

        // Generate order number
        String orderSn = IdWorker.getTimeId();

        // Build order entity
        OrderEntity orderEntity = buildOrder(orderSn);
        orderCreateTo.setOrder(orderEntity);

        // Get order items
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        orderCreateTo.setOrderItems(orderItemEntities);

        // Valid price
        computePrice(orderEntity, orderItemEntities);

        return orderCreateTo;
    }

    /**
     * Compute order price, saving amount etx
     * @param orderEntity
     * @param orderItemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        for (OrderItemEntity entity : orderItemEntities) {
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(entity.getRealAmount());
        }

        // Set price and savings
        orderEntity.setTotalAmount(total);
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);

        // Order point
        orderEntity.setGrowth(total.intValue());
        orderEntity.setIntegration(total.intValue());

        // Delete status, default = 0, no delete
        orderEntity.setDeleteStatus(0);

        //pay amount =  Order price + delivery fee
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
    }

    /**
     * Build order entity, include address order status etc
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(String orderSn) {

        MemberRespVo user = LoginUserInterceptor.loginUser.get();

        // New order entity
        OrderEntity entity = new OrderEntity();

        // Set order number
        entity.setOrderSn(orderSn);

        // Set user id
        entity.setMemberId(user.getId());

        /*
         * Get shipping address and delivery fee
         */
        OrderSubmitVo orderSubmitVo = orderThreadLocal.get();
        R fare = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResp = fare.getData(new TypeReference<FareVo>() {});

        // Delivery fee
        entity.setFreightAmount(fareResp.getFare());

        // City
        entity.setReceiverCity(fareResp.getAddress().getCity());
        // Detail Address
        entity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        // Province
        entity.setReceiverProvince(fareResp.getAddress().getProvince());
        // Name
        entity.setReceiverName(fareResp.getAddress().getName());
        // Phone
        entity.setReceiverPhone(fareResp.getAddress().getPhone());
        // Post Code
        entity.setReceiverPostCode(fareResp.getAddress().getPostCode());

        // Order status
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(7);

        return entity;
    }

    /**
     * Build order items and return as a list
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // Get new item price again
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0){

            List<OrderItemEntity> itemEntities = currentUserCartItems.stream().map(item -> {
                OrderItemEntity orderItemEntity = buildOrderItem(item);
                orderItemEntity.setOrderSn(orderSn);

                return orderItemEntity;
            }).collect(Collectors.toList());
            return itemEntities;
        }
        return null;
    }

    /**
     * Build every item in order
     * @param item
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity itemEntity = new OrderItemEntity();

        // 1. order number

        // 2. SPU info
        R r = productFeignService.getSpuInfoBySkuId(item.getSkuId());
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {});
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());

        // 3. SKU info
        itemEntity.setSkuId(item.getSkuId());
        itemEntity.setSkuName(item.getTitle());
        itemEntity.setSkuPic(item.getImage());
        itemEntity.setSkuPrice(item.getPrice());

        String skuAttr = StringUtils.collectionToDelimitedString(item.getAttrs(), ",");
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(item.getCount());
        itemEntity.setGiftGrowth(item.getPrice().intValue());
        itemEntity.setGiftIntegration(item.getPrice().intValue());

        // 4. Price
        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));

        BigDecimal origin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = origin.subtract(itemEntity.getCouponAmount())
                .subtract(itemEntity.getIntegrationAmount())
                .subtract(itemEntity.getPromotionAmount());
        itemEntity.setRealAmount(subtract);

        return itemEntity;
    }


}