package com.happymall.member.web;

import com.alibaba.fastjson.JSON;
import com.common.utils.R;
import com.happymall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yilong
 * @date 2022-10-25 3:43 p.m.
 * @description
 */

@Controller
public class MemberWebController {

    @Autowired
    OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value="pageNum", defaultValue="1") Integer pageNum,
                                  Model model) {

        Map<String, Object> page = new HashMap<>();
        page.put("page", pageNum.toString());

        R r = orderFeignService.listWithItem(page);
        System.out.println(JSON.toJSONString(r));
        model.addAttribute("orders",r);
        return "orderList";
    }

}
