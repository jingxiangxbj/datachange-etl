package com.jingxiang.datachange.util;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LoadUtils {



    private Long callTime;
    private String status;
    private String detail;
    private int id;
    private String name;
    private Integer age;
    private String birth;
    private String gender;
    private String mobile;
    private String address;
    private String idCard;
    private String stage;

    public void start(String status, String stage, String detail, String name, Integer age, String birth, String gender, String mobile,
                      String address, String idCard, int id, RestHighLevelClient client) throws IOException {
        callTime = Calendar.getInstance().getTimeInMillis();
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.birth = birth;
        this.mobile = mobile;
        this.idCard = idCard;
        this.status = status;
        this.stage = stage;
        this.detail = detail;
//        String indexName = getIndexName(callTime);
//        createIndex(client,indexName);
        printLog(client);
    }

    public void success( RestHighLevelClient client) throws IOException {
        this.status = "SUCCESS";
        this.detail = "访问成功";
        printLog(client);
    }

    public void fail(String detail, RestHighLevelClient client) throws IOException {
        this.status = "FAIL";
        this.detail = detail;
        printLog(client);
    }

    /**
     * 功能描述: 根据时间毫秒数来判断数据是属于几月份的索引
     *
     * @param: [millis]
     * @return: java.lang.String
     * @date: 2018-12-11 下午 07:17
     */
    private String getIndexName(Long millis) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String indexName = "load-" + format.format(millis);
        return indexName;
    }

    /**
     * 功能描述: 创建索引
     *
     * @param: [client, indexName]
     * @return: boolean
     * @date: 2018-12-11 下午 07:18
     */
    private boolean createIndex(RestHighLevelClient client, String indexName) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 5)
                .put("index.number_of_replicas", 1)
        );
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("doc");
            {
                builder.startObject("properties");
                {
                    builder.startObject("name");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                    builder.startObject("age");
                    {
                        builder.field("type", "Integer");
                    }
                    builder.endObject();
                    builder.startObject("gender");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                    builder.startObject("address");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                    builder.startObject("idCard");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                    builder.startObject("mobile");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                    builder.startObject("birth");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                    builder.startObject("detail");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                    builder.startObject("callTime");
                    {
                        builder.field("type", "date");
                    }
                    builder.endObject();
                    builder.startObject("status");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                    builder.startObject("id");
                    {
                        builder.field("type", "integer");
                    }
                    builder.endObject();
                    builder.startObject("uuid");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping("doc", builder);
        CreateIndexResponse createIndexResponse = client.indices().create(request);

        return createIndexResponse.isAcknowledged();
    }

    /**
     * 功能描述: 向索引种插入数据
     *
     * @param: [client]
     * @return: void
     * @date: 2018-12-11 下午 07:19
     */
    private void printLog(RestHighLevelClient client) throws IOException {
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("callTime", callTime);
            map.put("status", status);
            map.put("detail", detail);
            map.put("id", id);
            map.put("name", name);
            map.put("age", age);
            map.put("gender", gender);
            map.put("address", address);
            map.put("mobile", mobile);
            map.put("birth", birth);
            map.put("idCard", idCard);
            String indexName = this.getIndexName(callTime);
            Response response = client.getLowLevelClient().performRequest("HEAD", "/load");
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode == 404) {
            //触发创建索引的逻辑
//                boolean statu = this.createIndex(client, "load");
//                if (statu == true) {
//                    Indices indices = new Indices();
//                    indices.setOriginal_name(indexName);
//                    indices.setPresent_name(indexName);
//                    indicesService.add(indices);
//                }
//            }
            IndexRequest indexRequest = new IndexRequest("load", "doc").source(map);
            IndexResponse indexResponse = client.index(indexRequest);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // client.close();
        }

    }

}
