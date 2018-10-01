package top.huqj.blog.model.ext;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import top.huqj.blog.model.Category;

/**
 * 类别和对应的博客数量
 *
 * @author huqj
 */
@Data
@AllArgsConstructor
public class CategoryAndBlogNum {

    private Category category;

    private int blogNum;

}
