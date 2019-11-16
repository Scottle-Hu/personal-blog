package top.huqj.blog.dao;

import org.apache.ibatis.annotations.Param;
import top.huqj.blog.model.Remark;

import java.util.List;

/**
 * @author huqj
 */
public interface RemarkDao {

    int insertOne(Remark remark);

    List<Remark> findByArticleId(@Param("articleId") int articleId, @Param("articleType") int articleType);

    List<Remark> findById(int id);

    int countByArticleId(@Param("articleId")int articleId, @Param("articleType")int articleType);

    int deleteById(int id);

}
