package com.happymall.member.service.impl;

import com.happymall.member.dao.MemberLevelDao;
import com.happymall.member.entity.MemberLevelEntity;
import com.happymall.member.exception.PhoneExistException;
import com.happymall.member.exception.UsernameExistException;
import com.happymall.member.vo.MemberLoginVo;
import com.happymall.member.vo.MemberRegisterVo;
import com.happymall.member.vo.SocialUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.happymall.member.dao.MemberDao;
import com.happymall.member.entity.MemberEntity;
import com.happymall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo vo) {
        MemberEntity memberEntity = new MemberEntity();

        // Default level
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        // check username and phone
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUsername());

        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUsername());
        memberEntity.setNickname(vo.getUsername());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);

        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void  checkUsernameUnique(String username) throws UsernameExistException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0){
            throw  new UsernameExistException();
        }
    }

    @Override
    public void  checkPhoneUnique(String phone) throws PhoneExistException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0){
            throw new PhoneExistException();
        }

    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginaccount = vo.getLoginaccount();
        String password = vo.getPassword();

        MemberEntity memberEntity = this.baseMapper.selectOne(
                new QueryWrapper<MemberEntity>().eq("username", loginaccount).or().eq("mobile", loginaccount));
        if(memberEntity == null){
            // login fail
            return null;
        } else {
            String passwordDb = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matchResult = passwordEncoder.matches(password, passwordDb);
            if(matchResult){
                return memberEntity;
            } else {
                return null;
            }
        }

//        return null;
    }

    @Override
    public MemberEntity login(SocialUserDetails socialUser) {

        String uid = socialUser.getId();
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(memberEntity != null){
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(memberEntity.getAccessToken());

            this.baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            return memberEntity;
        } else {
            MemberEntity newUser = new MemberEntity();
            newUser.setNickname(socialUser.getLogin());
            newUser.setSocialUid(socialUser.getId());
            newUser.setAccessToken(socialUser.getAccess_token());
            this.baseMapper.insert(newUser);
            return newUser;
        }
    }

}