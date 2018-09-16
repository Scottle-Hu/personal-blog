package top.huqj.blog.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.huqj.blog.model.Category;

import java.sql.Date;
import java.util.List;

/**
 * @author huqj
 * @warning 不可自动化测试，真的会修改数据库
 */
@ContextConfiguration("classpath:applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CategoryDaoTest {

    @Autowired
    private CategoryDao categoryDao;

    private Category category;

    @Before
    public void init() {
        category = new Category();
        category.setName("java");
        category.setCreateTime(new Date(System.currentTimeMillis()));
        category.setUpdateTime(new Date(System.currentTimeMillis()));
    }

    @Test
    public void testInsertOne() {
        categoryDao.insertOne(category);
    }

    @Test
    public void testFindById() {
        Category c = categoryDao.findById(1);
        assert c.getId() == 1;
        assert "java".equals(c.getName());
    }

    @Test
    public void testUpdateOne() {
        category.setId(1);
        category.setName("java2");
        categoryDao.updateOne(category);
        Category c = categoryDao.findById(1);
        assert "java2".equals(c.getName());
    }

    @Test
    public void testDeleteById() {
        categoryDao.deleteById(1);
        Category c = categoryDao.findById(1);
        assert null == c;
    }

    @Test
    public void testFindAll() {
        List<Category> all = categoryDao.findAll();
        assert all.size() == 2;
    }

}