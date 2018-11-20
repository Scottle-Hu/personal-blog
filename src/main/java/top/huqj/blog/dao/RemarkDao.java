package top.huqj.blog.dao;

import top.huqj.blog.model.Remark;

import java.util.List;

/**
 * @author huqj
 */
public interface RemarkDao {

    int insertOne(Remark remark);

    List<Remark> findByArticleId(int articleId);

    List<Remark> findById(int id);

    int countByArticleId(int articleId);

    int deleteById(int id);

}
