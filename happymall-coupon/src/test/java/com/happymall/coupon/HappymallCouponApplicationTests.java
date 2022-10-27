package com.happymall.coupon;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.happymall.coupon.entity.CouponEntity;
import com.happymall.coupon.service.CouponService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;


@SpringBootTest
public class HappymallCouponApplicationTests {

    @Autowired
    CouponService couponService;

    @Test
    public void contextLoads() throws SQLException {


        List<CouponEntity> list = couponService.list(new QueryWrapper<CouponEntity>().eq("coupon_name",1));


        list.forEach((item) -> {
            System.out.println(item);
        });
//        Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.56.10:3306/happymall_sms",
//                "root","root");
//        System.out.println(conn);


    }

}
