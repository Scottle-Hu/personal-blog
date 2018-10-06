package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.huqj.blog.dao.CategoryDao;
import top.huqj.blog.model.Category;
import top.huqj.blog.service.ICategoryService;

/**
 * @author huqj
 */
@Service("categoryService")
@Log4j
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public Category findCategoryById(int id) {
        return categoryDao.findById(id);
    }

    @Override
    public void addCategory(Category category) {

    }

    @Override
    public void deleteCategory(int id) {

    }

    @Override
    public void updateCategory(Category category) {

    }
}
