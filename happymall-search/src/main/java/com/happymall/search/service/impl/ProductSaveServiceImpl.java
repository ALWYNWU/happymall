package com.happymall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.common.to.es.SkuEsModel;
import com.happymall.search.config.HappymallEsConfig;
import com.happymall.search.constant.EsConstant;
import com.happymall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yilong
 * @date 2022-10-10 2:23 p.m.
 * @description
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

        // Save data to elasticsearch
        // 1. Create index in es

        // 2. Save data
        BulkRequest bulkRequest = new BulkRequest();

        for (SkuEsModel model : skuEsModels) {
            // Construct es save request
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(model), XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, HappymallEsConfig.COMMON_OPTIONS);

        // TODO handle error
        boolean result = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> item.getId()).collect(Collectors.toList());

        log.info("Product activate successfully: {}, Return data: {}", collect, bulk.toString());

        return result;
    }
}
