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
     * 根据分页和博客类别获得博客列表
     *
     * @param page
     * @return
     */
    List<Blog> findLatestBlogByPageAndCategory(Map<String, Integer> page);

    /**
     * 根据分页信息和博客所属月份获取博客列表
     *
     * @param page
     * @return
     */
    List<Blog> findLatestBlogByPageAndMonth(Map<String, Object> page);

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

    /**
     * 获取前一篇博客
     *
     * @param id
     * @return
     */
    Blog getPrevious(int id);

    /**
     * 获取后一篇博客
     *
     * @param id
     * @return
     */
    Blog getNext(int id);

    /**
     * 获取某个类别的全部博客数量
     *
     * @param id
     * @return
     */
    int countByCategoryId(int id);

    /**
     * 获取某个月份博客的数量
     *
     * @param month
     * @return
     */
    int countByMonth(String month);
}
