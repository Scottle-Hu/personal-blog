package top.huqj.blog.dao;

import top.huqj.blog.model.Blog;

import java.util.List;
import java.util.Map;

/**
 * @author huqj
 */
public interface BlogDao {

    List<Blog> findById(int id);

    int insertOne(Blog blog);

    int updateOne(Blog blog);

    List<Blog> findLatestByPage(Map<String, Integer> page);

    //用于查找各种指标下的博客，使用redis存储id列表
    List<Blog> findByIdList(List<Integer> ids);


}
