package top.huqj.blog.service;

import top.huqj.blog.model.Category;

/**
 * 博客类别相关的操作
 *
 * @author huqj
 */
public interface ICategoryService {

    Category findCategoryById(int id);

    void addCategory(Category category);

    void deleteCategory(int id);

    void updateCategory(Category category);
    
}
