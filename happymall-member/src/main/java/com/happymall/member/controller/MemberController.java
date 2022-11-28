package com.happymall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.common.exception.BizCodeEnume;
import com.happymall.member.exception.PhoneExistException;
import com.happymall.member.exception.UsernameExistException;
import com.happymall.member.feign.CouponFeignService;
import com.happymall.member.vo.MemberLoginVo;
import com.happymall.member.vo.MemberRegisterVo;
import com.happymall.member.vo.SocialUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.happymall.member.entity.MemberEntity;
import com.happymall.member.service.MemberService;
import com.common.utils.PageUtils;
import com.common.utils.R;



/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 14:42:51
 */
@RestController
@RequestMapping("member/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeignService couponFeignService;

//    @RequestMapping("/coupons")
//    public R test(){
//        MemberEntity memberEntity = new MemberEntity();
//        memberEntity.setNickname("Nick");
//        R membercoupons = couponFeignService.memberCoupons();
//
//        return R.ok().put("member", memberEntity).put("coupon",membercoupons.get("coupon"));
//    }

    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo vo){

        try {
            memberService.register(vo);
        } catch (PhoneExistException e){
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameExistException e){
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(), BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }

        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){

        MemberEntity entity = memberService.login(vo);
        if (entity != null){
            return R.ok().setData(entity);
        } else {
            return R.error(15003,"Invalid username or password");
        }
    }

    @PostMapping("/oauth2/login")
    public R oauthlogin(@RequestBody SocialUserDetails socialUser){

        MemberEntity entity = memberService.login(socialUser);
        if (entity != null){
            return R.ok().setData(entity);
        } else {
            return R.error(15003,"Invalid username or password");
        }
    }


    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
