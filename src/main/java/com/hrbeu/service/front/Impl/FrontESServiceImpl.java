package com.hrbeu.service.front.Impl;

import com.hrbeu.service.front.FrontESService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class FrontESServiceImpl implements FrontESService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Override
    public List<Map<String, Object>> queryDocumentsByPage(String keyword, Integer pageIndex, Integer pageSize) {
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("documents");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("content", keyword);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("content");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.requireFieldMatch(false);
        if(pageIndex!=null&&pageSize!=null){
            sourceBuilder.query(queryBuilder).timeout(new TimeValue(5, TimeUnit.SECONDS)).from((pageIndex-1)*pageSize).size(pageSize).highlighter(highlightBuilder);
            searchRequest.source(sourceBuilder);
            SearchResponse result = null;
            try {
                result = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(result!=null){
                SearchHits hits = result.getHits();
                List<Map<String,Object>> contentList = new ArrayList<>();
                for (SearchHit hit : hits.getHits()) {
                    //解析高亮的字段
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    HighlightField titleHighLight = highlightFields.get("content");
                    Text[] fragments = titleHighLight.fragments();
                    StringBuffer sb = new StringBuffer();
                    for (Text fragment : fragments) {
                        sb.append(fragment);
                    }
                    Map<String,Object> resultMap = hit.getSourceAsMap();
                    resultMap.put("content",sb);
                    contentList.add(resultMap);
                }
                return contentList;
            }else {
                return null;
            }
        }else {
            Map<String,Object> lengthMap = new HashMap<>();
            sourceBuilder.query(queryBuilder).timeout(new TimeValue(5, TimeUnit.SECONDS));
            searchRequest.source(sourceBuilder);
            SearchResponse result = null;
            try {
                result = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<Map<String,Object>> lengthList = new ArrayList<>();
            if(result!=null){
                Integer length =  result.getHits().getHits().length;
                lengthMap.put("length",length);
                lengthList.add(lengthMap);
                return lengthList;
            }else {
                return null;
            }
        }

    }
}
