package top.huqj.blog.service;

import top.huqj.blog.model.UserInfo;

/**
 * @author huqj
 */
public interface IUserInfoService {

    UserInfo findById(String id);

    void insert(UserInfo userInfo);

    boolean isExistById(String id);

}
