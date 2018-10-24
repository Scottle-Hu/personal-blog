package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.huqj.blog.dao.CategoryDao;
import top.huqj.blog.model.Category;
import top.huqj.blog.model.ext.CategoryAndBlogNum;
import top.huqj.blog.service.ICategoryService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author huqj
 */
@Service("categoryService")
@Log4j
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private RedisManager redisManager;

    /**
     * 博客类别到博客id的对应关系列表名称的前缀，后面还需要加上类别id
     */
    @Value("${redis.list.blog.category2blog.prefix}")
    private String category2BlogIdsKeyPrefix;

    @Override
    public Category findCategoryById(int id) {
        return categoryDao.findById(id);
    }

    @Override
    public void addCategory(Category category) {
        categoryDao.insertOne(category);
    }

    @Override
    public void deleteCategory(int id) {
        String categoryKey = category2BlogIdsKeyPrefix + id;
        if (redisManager.getListLength(categoryKey) > 0) {
            log.warn("there are some blog in this category cannot delete.");
            return;
        }
        categoryDao.deleteById(id);
    }

    @Override
    public void updateCategory(Category category) {
        if (category == null) {
            return;
        }
        category.setUpdateTime(new Date(System.currentTimeMillis()));
        categoryDao.updateOne(category);
    }

    @Override
    public List<CategoryAndBlogNum> getAllCategoryList() {
        List<Category> categoryList = categoryDao.findAll();
        if (CollectionUtils.isEmpty(categoryList)) {
            return Collections.emptyList();
        }
        List<CategoryAndBlogNum> result = new ArrayList<>();
        for (Category category : categoryList) {
            //从redis中读取该类别的博客数量
            int num = (int) redisManager.getListLength(category2BlogIdsKeyPrefix + category.getId());
            result.add(new CategoryAndBlogNum(category, num));
        }
        return result;
    }
}
