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

    //获取总条数
    int count();

    //根据类别获取数量
    @Deprecated
    int countByCategoryId(int categoryId);

    /**
     * 获取当前最大的id
     *
     * @return
     */
    int maxId();

}
