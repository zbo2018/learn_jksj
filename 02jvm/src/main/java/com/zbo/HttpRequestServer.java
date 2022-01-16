package com.zbo;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;

public class HttpRequestServer {

    public static void main(String[] args) throws InterruptedException {
        String url = "http://localhost:8801";
        //String url = "http://localhost:8808";
        int socketTimeout = 5000;//5秒
        int connectTimeout = 5000;//5秒
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpEntity httpEntity = null;
        CloseableHttpResponse response = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout)
                    .build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);

            response = httpclient.execute(httpGet);
            System.out.println("HTTP Get 请求状态:"+  response.getStatusLine());
            response.setHeader("Content-Type", "text/html;charset=utf-8");

            httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity, Charset.forName("utf-8"));
            System.out.println("接收的结果：===========");
            System.out.println(result);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            //httpGet.abort();
            if(httpEntity!=null){
                try{
                    //一定要把entity fully consume掉，否则连接池中的connection就会一直处于占用状态
                    //等同主动关闭流 InputStream.close();
                    EntityUtils.consume(httpEntity);
                }catch (Exception e){
                    System.out.println("HTTP 消费响应内容异常");
                    e.printStackTrace();
                }
            }
            if(response!=null){
                try{
                    //close之前, 连接状态依旧为租赁状态(leased为false), 则该连接不被复用.
                    response.close();//确保连接放回连接池
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("HTTP 关闭响应流异常");
                }
            }
        }

    }
}
