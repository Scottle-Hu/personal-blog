package top.huqj.blog.lucene;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.dao.BlogDao;
import top.huqj.blog.model.Blog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * 为lucene搜索提供数据源，将数据库中的内容写到txt
 *
 * @author huqj
 */
@ContextConfiguration("classpath:applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConvertBlogContent2Txt {

    @Autowired
    private BlogDao blogDao;

    private String dataDir = "G:\\luceneTest\\data";

    @Test
    public void write() throws IOException {
        final int count = blogDao.count();
        List<Blog> blogList = blogDao.findLatestByPage(new HashMap<String, Integer>() {{
            put(BlogConstant.PAGE_OFFSET, 0);
            put(BlogConstant.PAGE_NUM, count);
        }});
        for (Blog blog : blogList) {
            String title = blog.getTitle().replace("<", "")
                    .replace(">", "")
                    .replace("%", "");
            File f = new File(dataDir + "\\" + title + ".txt");
            FileWriter writer = new FileWriter(f);
            writer.write(blog.getText());
            writer.close();
            System.out.println(title + "写入成功！");
        }
    }

}
