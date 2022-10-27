package com.happymall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.common.to.es.SkuEsModel;
import com.common.utils.R;
import com.happymall.search.config.HappymallEsConfig;
import com.happymall.search.constant.EsConstant;
import com.happymall.search.feign.ProductFeignService;
import com.happymall.search.service.MallSearchService;
import com.happymall.search.vo.*;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yilong
 * @date 2022-10-13 9:27 p.m.
 * @description
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Qualifier("esRestClient")
    @Autowired
    RestHighLevelClient client;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam param) {
        SearchResult result = null;

        SearchRequest searchRequest = buildSearchRequest(param);

        try {
            SearchResponse response = client.search(searchRequest, HappymallEsConfig.COMMON_OPTIONS);

            result = buildSearchResult(response, param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    private SearchRequest buildSearchRequest(SearchParam param) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        /**
         * match, filter (attributes, category, brand, stock, price)
         */
        // 1. bool query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 1.1 bool - must
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }

        // 1.2 bool -- filter category id
        if(param.getCatalog3Id() != null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        // 1.2 bool -- filter Brand id
        if(param.getBrandId() != null && param.getBrandId().size() > 0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }

        // 1.2 bool -- filter attributes
        if(param.getAttrs() != null && param.getAttrs().size() >0){

            // attrs = 1_5inch:8inch&attrs=...
            for(String attrStr : param.getAttrs()){

                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();

                String[] s = attrStr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");

                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));

                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);

                boolQueryBuilder.filter(nestedQuery);
            }

        }


        // 1.2 bool -- filter Has stock
        if(param.getHasStock() != null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock()==1));
        }



        // 1.2 bool -- filter Price range
        if(!StringUtils.isEmpty(param.getSkuPrice())){

            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");

            String[] s = param.getSkuPrice().split("_");
            if(s.length == 2){
                rangeQueryBuilder.gte(s[0]).lte(s[1]);
            } else if (s.length == 1){
                if(param.getSkuPrice().startsWith("_")){
                    rangeQueryBuilder.lte(s[0]);
                }
                if(param.getSkuPrice().endsWith("_")){
                    rangeQueryBuilder.gte(s[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);

        }

        sourceBuilder.query(boolQueryBuilder);

        /**
         * sort page
         */
        // 2.1 Sort
        if(!StringUtils.isEmpty(param.getSort())){
            String sort = param.getSort();
            // sort = price_asc/desc
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], order);
        }

        // 2.2 Page info
        // from = (pageNum - 1) * pageSize
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);



        // 2.3 highlight
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * aggregation
         */
        // Aggregate brand
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        // Sub aggregation of brand aggregation
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        /**
         * @param brand_agg
         * Aggregate brand info, brand img and brand name
         */
        sourceBuilder.aggregation(brand_agg);

        // Category agg
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(50);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));

        /**
         * @param catalog_agg
         * Aggregate category info
         */
        sourceBuilder.aggregation(catalog_agg);

        // Attribute agg
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");

        // Aggregate AttrId
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");

        // attrName corresponding to attrId
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));

        // attrValues corresponding to attrId
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));

        attr_agg.subAggregation(attr_id_agg);

        /**
         * @param attr_agg
         * Aggregate attribute info
         */
        sourceBuilder.aggregation(attr_agg);


        String s = sourceBuilder.toString();
//        System.out.println("DSL ====> "+s);

        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {

        SearchResult result = new SearchResult();

        // 1. Product info
        SearchHits hits = response.getHits();

        List<SkuEsModel> esModels = new ArrayList<>();
        if(hits.getHits() != null && hits.getHits().length > 0){
            for (SearchHit hit : hits.getHits()){
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);

                esModel.setSkuPrice(productFeignService.getPrice(esModel.getSkuId()).setScale(2));

                if(!StringUtils.isEmpty(param.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(string);
                }

                esModels.add(esModel);
            }
        }
        result.setProducts(esModels);

        // 2. Attribute info
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");

        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            // 1. Get AttrId
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            // 2. Get attribute name
            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            // 3. Get attribute value
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream()
                    .map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);



            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        // 3. Brands info
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();

            // 1. Get brand Id
            brandVo.setBrandId(bucket.getKeyAsNumber().longValue());

            // 2. Get brand name
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();

            // 3. Get brand img
            String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();

            brandVo.setBrandName(brandName);
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);


        // 4. category info
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();

            // Get category id
            long catalogId = bucket.getKeyAsNumber().longValue();
            catalogVo.setCatalogId(catalogId);

            // Get category name
//            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            R r = productFeignService.cateInfo(catalogId);
            CategoryEntityVo entity = r.getData("data", new TypeReference<CategoryEntityVo>() {});
            catalogVo.setCatalogName(entity.getName());

            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);

        // Page info, current page num
        result.setPageNum(param.getPageNum());

        // Total hits num
        long total = hits.getTotalHits().value;
        result.setTotal(total);

        // Total pages
        int totalPage = (int)total%EsConstant.PRODUCT_PAGESIZE ==
                0 ? (int)total/EsConstant.PRODUCT_PAGESIZE : ((int)total/EsConstant.PRODUCT_PAGESIZE+1);
        result.setTotalPages(totalPage);

        List<Integer> pageNavs = new ArrayList<Integer>();
        for (int i = 1; i<= totalPage; i++){
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);


        if(param.getAttrs() != null && param.getAttrs().size() > 0){
            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);

                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                result.getAttrIds().add(Long.parseLong(s[0]));
                if(r.getCode() == 0){
                    AttrRespVo data = r.getData("attr", new TypeReference<AttrRespVo>() {});
                    navVo.setNavName(data.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }

                String replace = replaceQueryString(param, attr,"attrs");
                navVo.setLink("http://search.happymall.com/list.html?"+replace);

                return navVo;
            }).collect(Collectors.toList());

            result.setNavs(navVos);
        }

        if(param.getBrandId() != null && param.getBrandId().size() > 0){
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("Brand");
            R r = productFeignService.infos(param.getBrandId());

            if(r.getCode() == 0){
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {});
                StringBuffer buffer = new StringBuffer();
                String replace = " ";
                for (BrandVo brandVo : brand) {
                    buffer.append(brandVo.getName()+";");
                    replace = replaceQueryString(param, brandVo.getBrandId()+"","brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.happymall.com/list.html?"+ replace);
            }
            navs.add(navVo);

        }

        return result;
    }

    private String replaceQueryString(SearchParam param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            // explorer will encode space to %20
            encode.replace("+","%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String replace = " ";
        if(!param.get_queryString().contains("&")){
            replace = param.get_queryString().replace("?"+key+"=" + encode, " ");
        } else {
            replace = param.get_queryString().replace("&"+key+"=" + encode, " ");
        }

        return replace;
    }
}
