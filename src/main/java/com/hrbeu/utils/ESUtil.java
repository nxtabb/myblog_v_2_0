package com.hrbeu.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrbeu.pojo.Document;
import com.hrbeu.pojo.User;
import com.hrbeu.pojo.vo.ESIndex;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.Date;

/**
 * @Classname ESUtil
 * @Description TODO
 * @Date 2021/5/17 15:38
 * @Created by nxt
 */
public class ESUtil {
    public static boolean addDocumentInES(Document document, RestHighLevelClient restHighLevelClient){
        ESIndex esDocument = new ESIndex(document.getDocumentId(),document.getTitle(),document.getContent(),document.getPublished(),document.getLastEditTime());
        //放入es
        BulkRequest bulkRequest = new BulkRequest();
        IndexRequest indexRequest = null;
        ObjectMapper mapper = new ObjectMapper();
        bulkRequest.timeout("5s");
        String documentJson = null;
        try {
            documentJson = mapper.writeValueAsString(esDocument);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        indexRequest = new IndexRequest("documents");
        indexRequest.source(documentJson, XContentType.JSON).id(document.getDocumentId()+"");
        bulkRequest.add(indexRequest);
        boolean result = false;
        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            result = response.hasFailures();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return result;
    }

    public static int deleteDocumentInES(Document document,RestHighLevelClient restHighLevelClient) {
        //编写请求
        DeleteRequest request = new DeleteRequest("documents", document.getDocumentId()+"");
        request.timeout("2s");
        //使用客户端发起请求
        DeleteResponse response = null;
        try {
            response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        }catch (Exception e){
            return 500;
        }
        return response.status().getStatus();
    }


    public static void updateDocumentInES(Document document,RestHighLevelClient restHighLevelClient){
        UpdateRequest request = new UpdateRequest("documents", document.getDocumentId()+"");
        ESIndex newDocument = new ESIndex(document.getDocumentId(),document.getTitle(),document.getContent(),document.getPublished(),document.getLastEditTime());
        ObjectMapper mapper = new ObjectMapper();
        String userJson = null;
        try {
            userJson = mapper.writeValueAsString(newDocument);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        request.timeout("5s");
        request.doc(userJson, XContentType.JSON);
        try {
            restHighLevelClient.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
