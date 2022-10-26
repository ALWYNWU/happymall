package com.happymall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-11 12:47 p.m.
 * @description
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CatelogTwoVo {

    private String catalog1Id; // Level 1 parent category
    private List<Catelog3Vo> catalog3List; // level 3 child category
    private String id;
    private String name;

    // Static internal class
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catelog3Vo {
        private String catalog2Id; // Level 2 parent category
        private String id;
        private String name;
    }

}
