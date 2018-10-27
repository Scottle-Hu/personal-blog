package top.huqj.blog.dao;

import top.huqj.blog.model.Category;

import java.util.List;

/**
 * @author huqj
 */
public interface CategoryDao {

    Category findById(int id);

    int insertOne(Category category);

    int updateOne(Category category);

    int deleteById(int id);

    List<Category> findAll();

    int count();

}
