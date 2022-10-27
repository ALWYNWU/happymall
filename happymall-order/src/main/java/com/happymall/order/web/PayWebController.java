package com.happymall.order.web;

import com.alipay.api.AlipayApiException;
import com.happymall.order.config.AlipayTemplate;
import com.happymall.order.service.OrderService;
import com.happymall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Yilong
 * @date 2022-10-25 3:17 p.m.
 * @description
 */
@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

        PayVo payVo = orderService.getOrderPay(orderSn);

        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);
        return pay;
    }

}
