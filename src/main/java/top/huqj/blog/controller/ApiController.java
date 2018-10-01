package top.huqj.blog.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.huqj.blog.service.IBlogService;

import java.util.HashMap;
import java.util.Map;

import static top.huqj.blog.controller.BlogController.ARTICLE_NUM_PER_PAGE;

/**
 * 非页面跳转类的api，包括md转html等ajax接口
 *
 * @author huqj
 */
@RestController
@Log4j
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private IBlogService blogService;

    @RequestMapping("/page")
    public Object getTotalPage() {
        Map<String, Integer> result = new HashMap<>();
        //给前端返回总页数
        int totalBlogNum = blogService.count();
        result.put("totalPage", totalBlogNum % ARTICLE_NUM_PER_PAGE == 0 ?
                totalBlogNum / ARTICLE_NUM_PER_PAGE : (totalBlogNum / ARTICLE_NUM_PER_PAGE) + 1);
        return result;
    }

}
