package top.huqj.blog.model.back;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 后台管理员
 *
 * @author huqj
 */
@Data
public class Admin {

    private int id;

    private String username;

    /**
     * 密码为md5 32位大写
     */
    private String password;

    private Timestamp createTime;

    private Timestamp updateTime;

}
