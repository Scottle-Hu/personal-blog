package top.huqj.blog.model;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * @author huqj
 */
@Data
@ToString
public class Remark {

    private int id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论文章id
     */
    private int articleId;

    /**
     * 评论者id
     */
    private String observerId;

    /**
     * 评论时间
     */
    private Timestamp publishTime;

}
