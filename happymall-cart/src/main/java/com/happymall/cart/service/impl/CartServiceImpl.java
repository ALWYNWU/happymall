package com.happymall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.common.utils.R;
import com.happymall.cart.feign.ProductFeignService;
import com.happymall.cart.interceptor.CartInterceptor;
import com.happymall.cart.service.CartService;
import com.happymall.cart.vo.Cart;
import com.happymall.cart.vo.CartItem;
import com.happymall.cart.vo.SkuInfoVo;
import com.happymall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author Yilong
 * @date 2022-10-19 8:25 p.m.
 * @description
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_PREFIX = "happymall:cart";


    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String res = (String) cartOps.get(skuId.toString());

        // This product doesn't exist in cart
        if(StringUtils.isEmpty(res)){

            CartItem cartItem = new CartItem();

            // RPC get product info
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R skuInfo = productFeignService.getSkuInfo(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {});

                cartItem.setChecked(true);
                cartItem.setCount(num);
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(data.getPrice());
            }, executor);

            // RPC get sale attribute info
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> saleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setAttrs(saleAttrValues);
            }, executor);

            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValues).get();
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        } else {
            //This product already exist in cart
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount()+num);

            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());

        return JSON.parseObject(o, CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        // Check login status
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null){
            // Has logged
            String cartKey = CART_PREFIX + userInfoTo.getUserId();

            // If temporary cart has items, merge them with logged user's cart
            String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempCartItems = getCartItems(tempCartKey);
            if (tempCartItems != null && tempCartItems.size() > 0){
                // Merge cart
                for (CartItem item : tempCartItems) {

                    /**
                     * addToCart will call getCartOps to get redis operation object, and getCartOps will
                     * check login status, it will get user id(logged) and add these items to a logged cart,
                     * so the logged cart in redis will add these item, after this, getCartItems will get all
                     * these items and return
                     */

                    addToCart(item.getSkuId(), item.getCount());
                }
                // Clear temporary cart
                clearCart(tempCartKey);
            }

            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);

        } else {
            // Hasn't logged, get temporary cart
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    /**
     * Get cart object in redis
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        // Get user info
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        String cartKey = "";
        if(userInfoTo.getUserId() != null){
            // User has already login, use login cart
            cartKey  = CART_PREFIX + userInfoTo.getUserId();
        } else {
            // Temporary cart
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }

        return redisTemplate.boundHashOps(cartKey);
    }

    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if(values != null && values.size() > 0){
            List<CartItem> collect = values.stream().map(obj -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItem.class);
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    @Override
    public void clearCart(String cartKey){
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer checked) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setChecked(checked == 1);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null){
            return null;
        } else {
            String carKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(carKey);
            List<CartItem> collect = cartItems.stream()
                    .filter(CartItem::getChecked)
                    .map(item -> {
                        // Query new price
                        item.setPrice(productFeignService.getPrice(item.getSkuId()));
                        return item;
                    })
                    .collect(Collectors.toList());

            return collect;
        }
    }
}
