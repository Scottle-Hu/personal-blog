package top.huqj.blog.model;

import lombok.Data;

import java.sql.Time;

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

    /**
     * 发布时间的字符串格式，用于显示
     */
    private String publishTimeStr;

    private Category category;

    /**
     * 用于在插入时表单提交参数
     */
    private int categoryId;

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
     * 博客内容的记录方式。1：图文编辑器  0:markdown
     */
    private int type;

    private Time updateTime;

    /**
     * 博客标签，用逗号隔开
     */
    private String tag;

    /**
     * 博客摘要封面图链接，以 | 分隔，在创建博客时提取并存储在数据库中
     */
    private String imgUrlList;

}
