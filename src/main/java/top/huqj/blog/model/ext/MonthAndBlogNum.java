package top.huqj.blog.model.ext;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 发布时间和该段时间博客数量
 *
 * @author huqj
 */
@AllArgsConstructor
@Data
public class MonthAndBlogNum {

    private String publishTime;

    private int blogNum;

}
