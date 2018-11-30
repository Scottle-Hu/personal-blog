package top.huqj.blog.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.misc.BASE64Encoder;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.model.UserInfo;
import top.huqj.blog.service.IUserInfoService;
import top.huqj.blog.service.impl.RedisManager;
import top.huqj.blog.utils.HttpUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录授权处理
 *
 * @author huqj
 */
@Controller
@Log4j
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private RedisManager redisManager;

    /**
     * 存储第三方登录session的key前缀
     */
    @Value("${redis.oauth.sessionkey}")
    private String SESSION_PREFIX;

    /**
     * oauth登录过期时间
     */
    @Value("${redis.oauth.expire}")
    private int SESSION_EXPIRE;

    /**
     * github获取access token的url，还需要末尾加上获取的code
     */
    @Value("${oauth.accesstoken.github.url}")
    private String GITHUB_ACCESS_TOKEN_URL_;

    /**
     * github获取用户信息的url，后面需要加上access token
     */
    @Value("${oauth.userinfo.github.url}")
    private String GITHUB_USER_INFO_URL_;

    /**
     * qqhuoqu access token的url，末尾需要加上code
     */
    @Value("${oauth.accesstoken.qq.url}")
    private String QQ_ACCESS_TOKEN_URL_;

    /**
     * qq获取用户id的url，后面需要加上access token
     */
    @Value("${oauth.me.qq.url}")
    private String QQ_ME_URL_;

    /**
     * qq获取用户信息的url，后面需要加上access token和用户id(&openid=&access_token=)
     */
    @Value("${oauth.userinfo.qq.url}")
    private String QQ_USER_INFO_URL;

    @Autowired
    private IUserInfoService userInfoService;

    /**
     * github登录接入回调处理
     *
     * @param request
     * @return
     */
    @RequestMapping("/github")
    public String githubOAuth(HttpServletRequest request, HttpServletResponse res) {
        try {
            //取得code
            String code = request.getParameter("code");
            //登陆前想要前往的url
            String originUrl = URLDecoder.decode(request.getParameter("state"), "utf-8");
            //请求access token
            String accessTokenUrl = GITHUB_ACCESS_TOKEN_URL_ + code;
            log.info("access token url: " + accessTokenUrl);
            Map<String, String> response = HttpUtil.postRequest(accessTokenUrl, new HashMap<>());
            if (response != null && response.get("access_token") != null) {
                //获取到access token，下面取得用户信息
                Map<String, String> githubUser = HttpUtil.getRequest(GITHUB_USER_INFO_URL_ + response.get("access_token"));
                log.info("user info: " + githubUser);
                UserInfo userInfo = UserInfo.buildFromGithub(githubUser);
                if (!userInfoService.isExistById(userInfo.getId())) {
                    userInfoService.insert(userInfo);
                }
                String key = SESSION_PREFIX + md5(userInfo.getId() + userInfo.getUsername() + userInfo.getRealName());
                redisManager.setWithExpireTime(key, userInfo.getId(), SESSION_EXPIRE);
                Cookie cookie = new Cookie(BlogConstant.OAUTH_SESSION_ID, key);
                cookie.setMaxAge(SESSION_EXPIRE);
                cookie.setPath("/");
                res.addCookie(cookie);
            } else {
                log.warn("can not get access token., response=" + response);
            }
            return "redirect:"
                    + originUrl.substring(originUrl.indexOf("/", originUrl.indexOf("://") + 3));
        } catch (Exception e) {
            log.error("error when oauth with github.", e);
        }
        return "redirect:/index";
    }

    /**
     * qq登录回调处理l
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/qq")
    public String qqOauth(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = request.getParameter("code");
            String originUrl = URLDecoder.decode(request.getParameter("state"), "utf-8");
            if (!StringUtils.isEmpty(code) && !StringUtils.isEmpty(originUrl)) {
                String accessTokenUrl = QQ_ACCESS_TOKEN_URL_ + code;  //换取access token
                String accessTokenResponse = HttpUtil.getPlainTextRequest(accessTokenUrl);
                if (!StringUtils.isEmpty(accessTokenResponse)) {
                    String accessToken = accessTokenResponse
                            .substring(accessTokenResponse.indexOf("access_token=") + 13, accessTokenResponse.indexOf("&"));
                    //获取用户openid
                    String userIdUrl = QQ_ME_URL_ + accessToken;
                    Map<String, String> userIdMap = HttpUtil.getRequest(userIdUrl);
                    if (userIdMap != null && userIdMap.get("openid") != null) {
                        String qqUserId = "qq_" + userIdMap.get("openid");
                        //TODO 注意这会导致信息的过时，之后需要改成记录上次登陆时间，定时更新，github也是
                        UserInfo qqUser = userInfoService.findById(qqUserId);
                        if (qqUser == null) {
                            String userInfoUrl = QQ_USER_INFO_URL +
                                    "&openid=" + userIdMap.get("openid") + "&access_token=" + accessToken;
                            Map<String, String> qqUserInfoMap = HttpUtil.getRequest(userInfoUrl);
                            qqUserInfoMap.put("id", qqUserId);
                            log.info("user info: " + qqUserInfoMap);
                            qqUser = UserInfo.buildFromQQ(qqUserInfoMap);
                            userInfoService.insert(qqUser);
                        }
                        String key = SESSION_PREFIX + md5(qqUser.getId() + qqUser.getUsername() + qqUser.getRealName());
                        redisManager.setWithExpireTime(key, qqUser.getId(), SESSION_EXPIRE);
                        Cookie cookie = new Cookie(BlogConstant.OAUTH_SESSION_ID, key);
                        cookie.setMaxAge(SESSION_EXPIRE);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                } else {
                    log.warn("can not get access token.");
                }
                return "redirect:"
                        + originUrl.substring(originUrl.indexOf("/", originUrl.indexOf("://") + 3));
            }
        } catch (Exception e) {
            log.error("error when oauth with qq.", e);
        }
        return "redirect:/index";
    }

    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        try {
            String originUrl = URLDecoder.decode(request.getParameter("originUrl"), "utf-8");
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(BlogConstant.OAUTH_SESSION_ID)) {
                    redisManager.deleteListAllValue(cookie.getValue());
                    break;
                }
            }
            return "redirect:"
                    + originUrl.substring(originUrl.indexOf("/", originUrl.indexOf("://") + 3));
        } catch (Exception e) {
            log.error("error logout.", e);
        }
        return "redirect:/index";
    }

    /**
     * md5加密算法
     *
     * @param str
     * @return
     */
    private String md5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BASE64Encoder base64en = new BASE64Encoder();
        String newstr = null;
        try {
            newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return newstr;
    }

}
