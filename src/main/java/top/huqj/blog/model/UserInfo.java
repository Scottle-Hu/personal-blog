package top.huqj.blog.model;

import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

import java.util.Map;

/**
 * 第三方接入的用户类，从第三方获取必要信息
 *
 * @author huqj
 */
@Data
@Log4j
@ToString
public class UserInfo {

    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像图片链接
     */
    private String iconUrl;

    /**
     * 第三方平台主页
     */
    private String _3rdPartyHomeUrl;

    /**
     * 第三方平台名称
     */
    private OAuthThirdParty _3rdParty;

    private String email;

    /**
     * 从github返回的用户信息构建一个用户
     *
     * @param map
     * @return
     */
    public static UserInfo buildFromGithub(Map<String, String> map) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId("github_" + String.valueOf(map.get("id")));
        userInfo.setUsername(map.get("login"));
        userInfo.setRealName(map.get("name"));
        userInfo.set_3rdParty(OAuthThirdParty.GITHUB);
        userInfo.setEmail(map.get("email"));
        userInfo.setIconUrl(map.get("avatar_url"));
        userInfo.set_3rdPartyHomeUrl(map.get("html_url"));
        return userInfo;
    }

    /**
     * 从qq返回的用户信息构建一个用户
     *
     * @param map
     * @return
     */
    public static UserInfo buildFromQQ(Map<String, String> map) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(map.get("id"));
        userInfo.setUsername(map.get("nickname"));
        userInfo.setRealName(null);
        userInfo.set_3rdParty(OAuthThirdParty.QQ);
        userInfo.setEmail(null);
        userInfo.setIconUrl(map.get("figureurl_1"));
        userInfo.set_3rdPartyHomeUrl("https://user.qzone.qq.com");  //TODO 暂时获取不到qq空间
        return userInfo;
    }

}
