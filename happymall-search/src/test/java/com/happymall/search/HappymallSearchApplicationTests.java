package com.happymall.search;

import com.alibaba.fastjson.JSON;
import com.common.utils.R;
import com.happymall.search.config.HappymallEsConfig;
import com.happymall.search.feign.ProductFeignService;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HappymallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    ProductFeignService productFeignService;

    /**
     * Search data
     * @throws IOException
     */
    @Test
    public void searchData() throws IOException{
        // 1. create query request
        SearchRequest request = new SearchRequest();

        // Assign index
        request.indices("bank");

        // Query condition
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        System.out.println(sourceBuilder.toString());

        // Aggregation according to age distribution
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(ageAgg);

        // Average aggregation of balance
        AvgAggregationBuilder balanceAgg = AggregationBuilders.avg("balanceAgg").field("balance");
        sourceBuilder.aggregation(balanceAgg);


        request.source(sourceBuilder);

        // Execute query
        SearchResponse response = client.search(request, HappymallEsConfig.COMMON_OPTIONS);

        // Result
        System.out.println(response.toString());
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();

        Aggregations aggregations = response.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {

            System.out.println("Age:" + bucket.getKeyAsString());
        }

        Avg balanceAvg1 =  aggregations.get("balanceAgg");
        System.out.println("average balance:" + balanceAvg1.getValue());



    }

    @Test
    public void testPrice(){
        R price = productFeignService.price(1L);
        System.out.println(price.get("skuPrice"));
    }

    /**
     * Save(index) data
     * @throws IOException
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest request = new IndexRequest("users");
        request.id("1");
//        request.source("userName","David", "age",18,"gender","M");
        User user = new User();
        user.setUsername("David");
        user.setAge(18);
        user.setGender("M");
        String jsonString = JSON.toJSONString(user);
        request.source(jsonString, XContentType.JSON);

        // Save operation
        IndexResponse index = client.index(request, HappymallEsConfig.COMMON_OPTIONS);

        System.out.println(index);

    }

    @Data
    class User{
        private String username;
        private String gender;
        private Integer age;
    }


    @Test
    public void contextLoads() {
        System.out.println(client);
    }

}
