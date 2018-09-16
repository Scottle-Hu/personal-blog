package top.huqj.blog.model;

import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

/**
 * 博客类别
 *
 * @author huqj
 */
@Data
@ToString
public class Category {

    private int id;

    private String name;

    private Date createTime;

    private Date updateTime;

}
