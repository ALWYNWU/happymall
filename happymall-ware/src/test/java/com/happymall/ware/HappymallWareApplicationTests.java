package com.happymall.ware;

import com.happymall.ware.service.WareSkuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HappymallWareApplicationTests {

    @Autowired
    WareSkuService wareSkuService;

    @Test
    public void contextLoads() {
        System.out.println(wareSkuService.getSkuHasStock(1L));


    }

}
