package top.huqj.blog.dao;

import top.huqj.blog.model.Essay;

import java.util.List;
import java.util.Map;

/**
 * @author huqj
 */
public interface EssayDao {

    List<Essay> findById(int id);

    int insertOne(Essay essay);

    int deleteOne(int id);

    int updateOne(Essay essay);

    List<Essay> findLatestByPageInfo(Map<String, Integer> map);

    List<Essay> findEssayByIdList(List<Integer> ids);

    int count();

    List<Essay> maxId();

    int addScanNum(int id);
}
