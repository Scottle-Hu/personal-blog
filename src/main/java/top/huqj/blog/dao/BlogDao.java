package top.huqj.blog.dao;

import top.huqj.blog.model.Blog;

import java.util.List;

/**
 * @author huqj
 */
public interface BlogDao {

    List<Blog> findById(int id);

    int insertOne(Blog blog);

    int updateOne(Blog blog);

    
}
