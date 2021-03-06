package top.huqj.blog.model;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 随笔
 *
 * @author huqj
 */
@Data
public class Essay {

    private int id;

    private String title;

    private String publishTimeStr;

    private Timestamp publishTime;

    private int scanNum;

    /**
     * 纯文本内容，没有标签和图片链接等
     */
    private String text;

    /**
     * markdown格式的内容
     */
    private String mdContent;

    /**
     * 展示的内容，为html格式
     */
    private String htmlContent;

    /**
     * 文章内容的记录方式。1：图文编辑器  0:markdown
     */
    private int type;

    private Timestamp updateTime;

    private String updateTimeStr;

}
