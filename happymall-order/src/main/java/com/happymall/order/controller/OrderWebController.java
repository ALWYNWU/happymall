package com.happymall.order.controller;

import com.happymall.order.service.OrderService;
import com.happymall.order.vo.OrderConfirmVo;
import com.happymall.order.vo.OrderSubmitVo;
import com.happymall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

/**
 * @author Yilong
 * @date 2022-10-22 5:55 p.m.
 * @description
 */

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);

        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){
        try {
            SubmitOrderResponseVo respVo = orderService.submitOrder(vo);
            if (respVo.getCode() == 0){
                // Success
                model.addAttribute("submitOrderResp", respVo);
                return "pay";
            } else {
                String msg = "Place Order Failed, ";
                switch (respVo.getCode()){
                    case 1: msg += "Please order again"; break;
                    case 2: msg += "Product price changed, Please order again"; break;
                    case 3: msg += "Insufficient stock"; break;
                }
                redirectAttributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.happymall.com/toTrade";
            }
        } catch (Exception e){
            return "redirect:http://order.happymall.com/toTrade";
        }

    }

}
