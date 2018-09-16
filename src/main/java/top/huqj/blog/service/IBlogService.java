package top.huqj.blog.service;

import top.huqj.blog.model.Blog;

import java.util.List;
import java.util.Map;

/**
 * @author huqj
 */
public interface IBlogService {

    Blog findBlogById(int id);

    void insertBlog(Blog blog) throws Exception;

    List<Blog> findLatestBlogByPage(Map<String, Integer> page);

}
