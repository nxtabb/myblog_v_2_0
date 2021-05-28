package com.hrbeu.conf;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname ESConfig
 * @Description TODO
 * @Date 2021/5/17 15:06
 * @Created by nxt
 */
@Configuration
public class ESConfig {
    //public final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//    @Bean
//    public RestHighLevelClient restHighLevelClient(){
//        credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("elastic", "nxtabb230105"));
//        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
//                RestClient.builder(new HttpHost("125.124.225.44",9200,"http")).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//                    @Override
//                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//                        httpClientBuilder.disableAuthCaching();
//                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//                    }
//                })
//        );
//        return restHighLevelClient;

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("125.124.225.44",9200,"http")));
        return restHighLevelClient;
    }
}
