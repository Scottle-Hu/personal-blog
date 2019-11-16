package top.huqj.blog.service;

import top.huqj.blog.model.Remark;

import java.util.List;

/**
 * @author huqj
 */
public interface IRemarkService {

    Remark findById(int id);

    List<Remark> findByArticleId(int articleId, int articleType);

    void insert(Remark remark);

    int countByArticleId(int articleId, int articleType);

    void deleteById(int id);

}
