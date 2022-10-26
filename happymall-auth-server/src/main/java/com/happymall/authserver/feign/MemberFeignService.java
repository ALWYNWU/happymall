package com.happymall.authserver.feign;

import com.common.utils.R;
import com.happymall.authserver.vo.SocialUserDetails;
import com.happymall.authserver.vo.UserLoginVo;
import com.happymall.authserver.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Yilong
 * @date 2022-10-17 7:27 p.m.
 * @description
 */
@FeignClient("happymall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    R oauthlogin(@RequestBody SocialUserDetails socialUser);


}
