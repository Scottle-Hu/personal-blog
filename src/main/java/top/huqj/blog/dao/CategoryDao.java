package top.huqj.blog.dao;

import top.huqj.blog.model.Category;

/**
 * @author huqj
 */
public interface CategoryDao {

    Category findById(int id);

    int insertOne(Category category);

    int updateOne(Category category);

}