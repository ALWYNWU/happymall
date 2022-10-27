package com.happymall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.common.utils.R;
import com.common.vo.MemberRespVo;
import com.happymall.authserver.feign.MemberFeignService;
import com.happymall.authserver.vo.UserLoginVo;
import com.happymall.authserver.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Yilong
 * @date 2022-10-16 7:41 p.m.
 * @description
 */
@Controller
public class LoginController {

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object loginUser = session.getAttribute("loginUser");
        if(loginUser == null){
            return "login";
        } else {
            return "redirect:http://auth.happymall.com";
        }

    }

    @GetMapping("/register.html")
    public String registerPage(){
        return "register";
    }

    @PostMapping("/signup")
    public String register(@Valid UserRegisterVo vo, BindingResult result,
                           RedirectAttributes redirectAttributes){
        if(result.hasErrors()){

            Map<String, String> errors =
                    result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));

            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.happymall.com/register.html";
        }

        R r = memberFeignService.register(vo);
        if(r.getCode() == 0){
            return "redirect:http://auth.happymall.com/login.html";
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", (String) r.get("msg"));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.happymall.com/register.html";
        }

    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session){

        R login = memberFeignService.login(vo);
        if(login.getCode() == 0){
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {});
            session.setAttribute("loginUser",data);
            return "redirect:http://happymall.com";
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", (String) login.get("msg"));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.happymall.com/login.html";
        }


    }

}
