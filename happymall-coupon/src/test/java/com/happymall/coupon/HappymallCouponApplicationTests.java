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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@SpringBootTest
public class HappymallCouponApplicationTests {

    @Autowired
    CouponService couponService;

    @Test
    public void contextLoads() throws SQLException {
        System.out.println(LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MAX)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

}
