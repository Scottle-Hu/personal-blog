package top.huqj.blog.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
public class HttpUtilTest {

    private String testURL = "https://github.com/login/oauth/access_token?client_id=7cc6cce09d315f877b82" +
            "&client_secret=feeee0ab62a12190db39aaa0d2c71eace234eff9&code=0f12e191e4fa76e3be16" +
            "&redirect_uri=http%3a%2f%2fwww.huqj.top%2foauth%2fgithub&format=json";

    @Test
    public void postRequest() throws Exception {
        Map<String, String> response = HttpUtil.postRequest(testURL, new HashMap<>());
        System.out.println(response);
        assert response.size() == 3;
        assert response.get("error") != null;
    }

}