package com.happymall.member;


import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HappymallMemberApplicationTests {

    @Test
    public void contextLoads() {

        class a{
            public void cal(int[] x){
                int i;
                int temp;
                int numItems = x.length;
                if (x.length == 0){
                    return;
                }
                temp = x[0];
                for (i = 0; i <= numItems-2; i++){
                    x[i] = x[i+1];
                }
                x[numItems-1] = temp;
                for (int x1 : x) {
                    System.out.println(x1);
                }
            }

        }
        a a = new a();
        a.cal(new int[]{7,3,8,1,2,5});


    }

}
