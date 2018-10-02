package top.huqj.blog.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import top.huqj.blog.dao.back.AdminDao;
import top.huqj.blog.model.back.Admin;

/**
 * shiro自定义realm
 *
 * @author huqj
 */
public class MyRealm implements Realm {

    @Autowired
    private AdminDao adminDao;

    @Override
    public String getName() {
        return "myUsernameAndPasswordRealm";
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof UsernamePasswordToken;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();
        String password = new String((char[]) authenticationToken.getCredentials());
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {  //用户名或者密码不存在
            throw new UnknownAccountException();
        }
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        if (adminDao.findByUsernameAndPassword(admin) != 1) {  //验证失败
            throw new IncorrectCredentialsException();
        } else {
            return new SimpleAuthenticationInfo(username, password, getName());
        }
    }
}
