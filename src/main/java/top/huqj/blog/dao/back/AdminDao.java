package top.huqj.blog.dao.back;

import top.huqj.blog.model.back.Admin;

/**
 * @author huqj
 */
public interface AdminDao {

    Admin findById(int id);

    int findByUsernameAndPassword(Admin admin);

    int insertOne(Admin admin);

    int updateOne(Admin admin);

    int deleteOne(int id);
}
