package com.hrbeu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrbeu.dao.admin.*;
import com.hrbeu.pojo.Document;
import com.hrbeu.pojo.Tag;
import com.hrbeu.pojo.Type;
import com.hrbeu.pojo.vo.ESIndex;
import com.hrbeu.pojo.vo.SavePath;
import com.hrbeu.utils.PathUtil;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
class MyblogV20ApplicationTests {
    @Autowired
    private AdminDocumentDao adminDocumentDao;
    @Autowired
    private AdminTypeDao adminTypeDao;
    @Autowired
    private SavePath savePath;
    @Autowired
    private AdminCommentDao adminCommentDao;
    @Autowired
    private AdminTagDao adminTagDao;
    @Autowired
    private AdminDocumentTagDao adminDocumentTagDao;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {
        List<Document> documents = adminDocumentDao.queryAllDocumentByPage(0, 5);
        for (Document document : documents) {
            System.out.println(document);
        }

        System.out.println(adminDocumentDao.queryAllDocumentCount());
    }

    @Test
    public void test01(){
        List<Tag> tagList = adminTagDao.queryTagsByIds(Arrays.asList(1L, 2L, 3L, 4L));
        for (Tag t : tagList) {
            System.out.println(t);
        }
    }
    @Test
    public void test02(){
        Document document = adminDocumentDao.queryDocumentAndUserAndTagsById(66L);
        System.out.println(document);
    }


    @Test
    public void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("documents");
        restHighLevelClient.indices().delete(request,RequestOptions.DEFAULT);
    }
    @Test
    public void createIndex() throws IOException {
        //创建一个 创建索引的请求
        CreateIndexRequest request = new CreateIndexRequest("documents");
        //客户端执行请求,并返回响应
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

}
