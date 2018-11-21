package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.huqj.blog.dao.UserInfoDao;
import top.huqj.blog.model.UserInfo;
import top.huqj.blog.service.IUserInfoService;

import java.util.List;

/**
 * @author huqj
 */
@Service("userInfoService")
@Log4j
public class UserInfoServiceImpl implements IUserInfoService {

    @Autowired
    private UserInfoDao userInfoDao;

    @Override
    public UserInfo findById(String id) {
        List<UserInfo> userInfoList = userInfoDao.findById(id);
        if (userInfoList.isEmpty() || userInfoList.size() > 1) {
            return null;
        }
        return userInfoList.get(0);
    }

    @Override
    public void insert(UserInfo userInfo) {
        userInfoDao.insertOne(userInfo);
    }

    @Override
    public boolean isExistById(String id) {
        return userInfoDao.countById(id) == 1;
    }
    
}
