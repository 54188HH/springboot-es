package com.lzq.springbootes.config;

import com.alibaba.fastjson.JSON;
import com.lzq.springbootes.entity.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;


/**
 * @program: springboot-es
 * @description:
 * @author: liuzhenqi
 * @create: 2020-07-31 10:48
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {
    @Autowired
    @Qualifier("restHighLevelClient")
    RestHighLevelClient client;

    /**
     * 创建索引
     */
    @org.junit.Test
    public void testCreateIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("test");
        CreateIndexResponse response = client.indices().create(request,RequestOptions.DEFAULT);
        System.out.println(response);
    }
    /**
     * 测试索引是否存在
     */
    @org.junit.Test
    public void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("test");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }
    /**
     * 删除索引
     */
    @org.junit.Test
    public void delIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("test");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     * 添加数据到es
     * @throws IOException
     */
    @org.junit.Test
    public void testIndex() throws IOException {
        User user = new User("lzq",  1L,18);

        //创建连接
        IndexRequest request = new IndexRequest("test");

        //规则put /test/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueDays(1));
        request.timeout("1s");

        request.source(JSON.toJSONString(user),XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
    }
    /**
     * 从es里查询数据
     */
    @org.junit.Test
    public void testGetDocument() throws IOException {
        GetRequest test = new GetRequest("test", "1");
        GetResponse response = client.get(test, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        System.out.println(response);
    }
    /**
     * 从es里g更新文档数据
     */
    @org.junit.Test
    public void testUpdateDocument() throws IOException {
        UpdateRequest re = new UpdateRequest("test", "1");
        User user = new User("刘振奇", 1L, 19);
        re.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse update = client.update(re, RequestOptions.DEFAULT);
    }

    /**
     * 从es里删除文档数据
     */
    @org.junit.Test
    public void testDelDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("test", "1");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
    }
    /**
     * 吧数据从mysql查出来然后批量导入es数据
     */
    @org.junit.Test
    public void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        ArrayList<User> list = new ArrayList<>();
        list.add(new User("lzq",1L,18));
        list.add(new User("ldl",2L,22));
        list.add(new User("lb",3L,21));
        list.add(new User("lya",4L,20));
        list.add(new User("lwl",5L,18));
        list.add(new User("ljx",6L,13));

        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(new IndexRequest("test")
                    .id(String.valueOf(list.get(i).getId()))
                    .source(JSON.toJSONString(list.get(i)),XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        //是否失败 返回false代表全部成功
        System.out.println(bulk.hasFailures());
    }
}
