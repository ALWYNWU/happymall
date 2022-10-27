package com.happymall.search.service;

import com.happymall.search.vo.SearchParam;
import com.happymall.search.vo.SearchResult;

/**
 * @author Yilong
 * @date 2022-10-13 9:27 p.m.
 * @description
 */
public interface MallSearchService {

    SearchResult search(SearchParam param);

}
