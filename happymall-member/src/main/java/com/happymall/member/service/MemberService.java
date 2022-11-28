package com.happymall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.happymall.member.entity.MemberEntity;
import com.happymall.member.exception.PhoneExistException;
import com.happymall.member.exception.UsernameExistException;
import com.happymall.member.vo.MemberLoginVo;
import com.happymall.member.vo.MemberRegisterVo;
import com.happymall.member.vo.SocialUserDetails;

import java.util.Map;

/**
 * @author YILONG
 * @email yilongwu97@gmail.com
 * @date 2022-09-05 14:42:51
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo vo);

    void checkUsernameUnique(String username) throws UsernameExistException;

    void checkPhoneUnique(String phone) throws PhoneExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUserDetails socialUser);
}


