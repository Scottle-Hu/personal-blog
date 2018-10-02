package top.huqj.blog.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.huqj.blog.service.IBlogService;

import java.util.HashMap;
import java.util.Map;

/**
 * 非页面跳转类的api，包括md转html等ajax接口
 *
 * @author huqj
 */
@RestController
@Log4j
@RequestMapping("/api")
public class ApiController {

    @Value("${maxBlogNumPerPage}")
    public int Blog_NUM_PER_PAGE;

    @Value("${maxEssayNumPerPage}")
    public int Essay_NUM_PER_PAGE;

    @Autowired
    private IBlogService blogService;

    @RequestMapping("/blog/page")
    public Object getTotalPage() {
        Map<String, Integer> result = new HashMap<>();
        //给前端返回总页数
        int totalBlogNum = blogService.count();
        result.put("totalPage", totalBlogNum % Blog_NUM_PER_PAGE == 0 ?
                totalBlogNum / Blog_NUM_PER_PAGE : (totalBlogNum / Blog_NUM_PER_PAGE) + 1);
        return result;
    }

}
