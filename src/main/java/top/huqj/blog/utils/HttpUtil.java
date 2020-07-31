package top.huqj.blog.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装http请求操作
 *
 * @author huqj
 */
@Log4j
public class HttpUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    //发送http请求的超时配置
    private static RequestConfig requestConfig =
            RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(1000)
                    .setSocketTimeout(5000)
                    .build();

    /**
     * 发出post请求并获取响应的json形式
     *
     * @param url
     * @param param
     * @return
     */
    public static Map<String, String> postRequest(String url, Map<String, String> param) {
        String content = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            //构造一级请求的url
            HttpPost post = new HttpPost(url);
            //设置请求头信息，防止中文乱码
            post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            post.setHeader("Accept", "application/vnd.github.v3+json");
            //创建参数列表
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> e : param.entrySet()) {
                list.add(new BasicNameValuePair(e.getKey(), e.getValue()));
            }
            //url格式编码
            UrlEncodedFormEntity uefEntity = null;
            try {
                uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            post.setEntity(uefEntity);
            HttpResponse response = null;
            try {
                response = client.execute(post);
                log.info("response status code: " + response.getStatusLine().getStatusCode());
            } catch (IOException e) {
                log.error("使用httpclient请求接口出现问题");
                e.printStackTrace();
            }
            HttpEntity entity = response.getEntity();
            try {
                content = EntityUtils.toString(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.error("error send post request.", e);
            return null;
        }

        try {
            content = new String(content.getBytes("iso-8859-1"), "utf-8");
            return mapper.readValue(content, Map.class);
        } catch (Exception e) {
            //通过请求头没法解决乱码问
            log.error("error convert text to json map.", e);
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, String> getRequest(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            //构造一级请求的url
            HttpGet get = new HttpGet(url);
            //设置请求头信息，防止中文乱码
            get.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            get.setHeader("Accept", "application/vnd.github.v3+json");
            //设置请求超时时间（踩坑CLOSE_WAIT，不确定是不是这个导致的）
            get.setConfig(requestConfig);
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            content = new String(content.getBytes("iso-8859-1"), "utf-8");
            /*
            兼容qq的返回数据格式：
            callback(
            {
            client_id: "101529384",
            openid: "8B61D4DB1215EA80480E504064B0345E"
            }
            )
             */
            if (content.startsWith("callback(")) {
                content = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
            }
            return mapper.readValue(content, Map.class);
        } catch (Exception e) {
            log.error("error when send GET request to get response. url=" + url, e);
        }
        return null;
    }

    public static String getPlainTextRequest(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            //构造一级请求的url
            HttpGet get = new HttpGet(url);
            //设置请求头信息，防止中文乱码
            get.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            get.setHeader("Accept", "application/vnd.github.v3+json");
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            content = new String(content.getBytes("iso-8859-1"), "utf-8");
            return content;
        } catch (Exception e) {
            log.error("error when send GET request to get response. url=" + url, e);
        }
        return null;
    }


}
