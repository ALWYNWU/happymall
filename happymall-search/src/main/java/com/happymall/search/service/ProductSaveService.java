package com.happymall.search.service;

import com.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author Yilong
 * @date 2022-10-10 2:20 p.m.
 * @description
 */

public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;

}
