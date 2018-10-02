package top.huqj.blog.controller.back;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import top.huqj.blog.service.IBlogService;

import javax.servlet.http.HttpServletRequest;

/**
 * 后台管理主控制器
 *
 * @author huqj
 */
@Log4j
@Controller
@RequestMapping("/back")
public class BackController {

    @Autowired
    private IBlogService blogService;

    @RequestMapping("/")
    public String defaultIndexPage(HttpServletRequest request) {
        return loginPage(request);
    }

    @RequestMapping("/login")
    public String loginPage(HttpServletRequest request) {
        return "back/login";
    }

    @RequestMapping("/check")
    public String checkLogin(HttpServletRequest request) {
        //验证错误会到此controller
        String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
        if (!StringUtils.isEmpty(exceptionClassName)) {
            if (exceptionClassName.equals(UnknownAccountException.class.getName())) {
                request.setAttribute("errMsg", "用户不存在");
            } else if (exceptionClassName.equals(IncorrectCredentialsException.class.getName())) {
                request.setAttribute("errMsg", "密码错误");
            } else {
                request.setAttribute("errMsg", "未知登录错误");
            }
        }
        return "back/login";
    }

    @RequestMapping("/console")
    public String consolePage(HttpServletRequest request) {
        //TODO
        return "back/console";
    }


}
