package top.huqj.blog.service;

import top.huqj.blog.model.Blog;
import top.huqj.blog.model.ext.CategoryAndBlogNum;
import top.huqj.blog.model.ext.MonthAndBlogNum;

import java.util.List;
import java.util.Map;

/**
 * @author huqj
 */
public interface IBlogService {

    Blog findBlogById(int id);

    void insertBlog(Blog blog) throws Exception;

    /**
     * 按照分页获取最新博客
     *
     * @param page
     * @return
     */
    List<Blog> findLatestBlogByPage(Map<String, Integer> page);

    /**
     * 计算所有博客数量，用于分页
     *
     * @return
     */
    int count();

    /**
     * 获取所有类别和对应的博客数量
     *
     * @return
     */
    List<CategoryAndBlogNum> getAllCategoryList();

    /**
     * 获取所有月份对应的博客数量
     *
     * @return
     */
    List<MonthAndBlogNum> getAllMonthList();

}
