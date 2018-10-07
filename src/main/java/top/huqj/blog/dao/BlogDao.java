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

    int deleteOne(int id);

    List<Blog> findLatestByPage(Map<String, Integer> page);

    //用于查找各种指标下的博客，使用redis存储id列表
    List<Blog> findByIdList(List<Integer> ids);

    //按浏览量降序排列
    List<Blog> findByIdListOrderedScanNum(List<Integer> ids);

    //TODO 按评论量降序排列
    List<Blog> findByIdListOrderedRemarkNum(List<Integer> ids);

    //获取总条数
    int count();

    //根据类别获取数量
    @Deprecated
    int countByCategoryId(int categoryId);

    /**
     * 获取当前最大的id
     * 因为数据库为空时会有问题，不能直接返回int
     *
     * @return
     */
    List<Blog> maxId();

    List<String> getTopNewBlog(int limit);

    List<Blog> getTopScanBlog(int limit);

    List<Blog> getTopRemarkBlog(int limit);

    /**
     * 浏览博客一次
     *
     * @param id
     * @return
     */
    int scanOnce(int id);

}
