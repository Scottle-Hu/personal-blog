package top.huqj.blog.service;

import top.huqj.blog.model.Essay;
import top.huqj.blog.model.ext.MonthAndEssayNum;

import java.util.List;
import java.util.Map;

/**
 * @author huqj
 */
public interface IEssayService {

    Essay findById(int id);

    void insertOne(Essay essay);

    void updateOne(Essay essay);

    void deleteById(int id);

    List<Essay> findEssayByIdList(List<Integer> ids);

    int count();

    int countByMonth(String month);

    /**
     * 获取按照月份统计的随笔数量
     *
     * @return
     */
    List<MonthAndEssayNum> getAllMonthAndEssayNum();

    /**
     * 获取某个月随笔的列表
     *
     * @param month
     * @return
     */
    List<Essay> getEssayListByMonth(String month);

    List<Essay> findLatestEssayListByPageInfo(Map<String, Integer> map);

    List<Essay> findLatestEssayListByPageInfoAndMonth(Map<String, Object> map);

    Essay getPreviousEssay(int id);

    Essay getNextEssay(int id);

}
