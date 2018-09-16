package top.huqj.blog.model;

import lombok.Data;

import java.sql.Time;
import java.util.List;

/**
 * 博客
 *
 * @author huqj
 */
@Data
public class Blog {

    private int id;

    private String title;

    private Time publishTime;

    private Category category;

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
     * 博客内容的记录方式。0：图文编辑器  1:markdown
     */
    private int type;

    private Time updateTime;

    /**
     * 博客标签，用逗号隔开
     */
    private String tag;

    /**
     * 博客摘要封面图链接，以 | 分隔
     */
    private String imgUrlList;

}
