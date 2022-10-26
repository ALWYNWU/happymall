package com.happymall.ware;

<<<<<<< HEAD

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

=======
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HappymallWareApplicationTests {

    @Test
    public void contextLoads() {
>>>>>>> 70dfac08787284ad44496d1cdd80f3e135a5c2fd
    }

}
