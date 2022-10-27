package com.happymall.authserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.common.utils.HttpUtils;
import com.common.utils.R;
import com.happymall.authserver.feign.MemberFeignService;
import com.common.vo.MemberRespVo;
import com.happymall.authserver.vo.SocialUser;
import com.happymall.authserver.vo.SocialUserDetails;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yilong
 * @date 2022-10-18 2:51 a.m.
 * @description
 */
@Controller
public class OAuth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/github/success")
    public String github(@RequestParam("code") String code, HttpSession session, HttpServletResponse servletResponse) throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("client_id","951fa42b13a534a9d821");
        map.put("client_secret","f5f4c1a5425e44643c4e1be8dcecb43c2a920699");
        map.put("redirect_uri","http://auth.happymall.com/oauth2.0/github/success");
        map.put("code",code);


        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept","application/json");

        // Get accessToken according to code  Accept: application/json
        HttpResponse response =
                HttpUtils.doPost("https://github.com", "/login/oauth/access_token", "post", headerMap, null, map);

        if(response.getStatusLine().getStatusCode() == 200){
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            String access_token = socialUser.getAccess_token();

            Map<String, String> tokenHeaderMap = new HashMap<>();
            tokenHeaderMap.put("Authorization", "Bearer " + access_token);
            HttpResponse infoResponse = HttpUtils.doGet("https://api.github.com", "/user", "get", tokenHeaderMap, null);

            if(infoResponse.getStatusLine().getStatusCode() == 200){
                String infoJson = EntityUtils.toString(infoResponse.getEntity());
                SocialUserDetails socialUserDetails = JSON.parseObject(infoJson, SocialUserDetails.class);
                socialUserDetails.setAccess_token(access_token);

                //TODO login or register this user
                R r = memberFeignService.oauthlogin(socialUserDetails);
                if(r.getCode()==0){
                    MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {});
                    session.setAttribute("loginUser",data);
//                    servletResponse.addCookie();
                    return "redirect:http://happymall.com";
                } else {
                    return "redirect:http://auth.happymall.com/login.html";
                }
            } else {
                return "redirect:http://auth.happymall.com/login.html";
            }

        } else {
            return "redirect:http://auth.happymall.com/login.html";
        }
    }

}
