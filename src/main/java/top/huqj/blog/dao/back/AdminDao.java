package top.huqj.blog.dao.back;

import top.huqj.blog.model.back.Admin;

import java.util.List;

/**
 * @author huqj
 */
public interface AdminDao {

    Admin findById(int id);

    int findByUsernameAndPassword(Admin admin);

    List<Admin> getAll();

    int insertOne(Admin admin);

    int updateOne(Admin admin);

    int deleteOne(int id);
}
