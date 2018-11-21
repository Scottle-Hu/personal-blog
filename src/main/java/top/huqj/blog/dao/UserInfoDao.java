package top.huqj.blog.dao;

import top.huqj.blog.model.UserInfo;

import java.util.List;

/**
 * @author huqj
 */
public interface UserInfoDao {

    int insertOne(UserInfo userInfo);

    List<UserInfo> findById(String id);

    int count();

    int countById(String id);
}
