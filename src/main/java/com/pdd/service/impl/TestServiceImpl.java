package com.pdd.service.impl;

import com.pdd.service.TestService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestServiceImpl implements TestService {

    private static final String TYPE = "pdd.ddk.goods.search";
    private static final String DATA_TYPE = "JSON";
    private static final String CLIENT_ID = "42543f5de30b4f60a276a58c062ad105";
    private static final String CLIENT_SECRET = "2588f5de4f6efb160428c2d46a0fc032be1f79b0";
    private static final String ACCESS_TOKEN = "你的access_token";
    private static final String PDD_API_URL = "https://gw-api.pinduoduo.com/api/router";

    /**
     * 测试pdd接口
     */
    @Override
    public String test() throws URISyntaxException, IOException {

        Long timeStamp = System.currentTimeMillis() / 1000;

        //构建URI
        URIBuilder uriBuilder = new URIBuilder(PDD_API_URL);
        uriBuilder.addParameter("type", TYPE);
        uriBuilder.addParameter("data_type", DATA_TYPE);
        uriBuilder.addParameter("client_id", CLIENT_ID);
        uriBuilder.addParameter("timestamp", timeStamp.toString());

        //获取sign
        Map<String, String> map = new HashMap<>();
        map.put("type", TYPE);
        map.put("data_type", DATA_TYPE);
        map.put("client_id", CLIENT_ID);
        map.put("timestamp", timeStamp.toString());
        String sign = generateSign(map);

        uriBuilder.addParameter("sign", sign);

        System.out.println(uriBuilder.toString());
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        //发送请求
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);

        //解析请求
        HttpEntity entity = response.getEntity();
        String responseEntity = EntityUtils.toString(entity);
        System.out.println(responseEntity);

        return responseEntity;

    }


    public String generateSign(Map<String, String> params){

        //根据key排序
        List<Map.Entry<String, String>> list = new ArrayList<>(params.entrySet());
        list.sort(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER));

        //进行拼接
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : list) {
            String s = entry.getKey() + entry.getValue();
            sb.append(s);
        }
        sb.insert(0, CLIENT_SECRET);
        sb.append(CLIENT_SECRET);

        //md5
        return md5(sb.toString()).toUpperCase();
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 算法不存在", e);
        }
    }



}
