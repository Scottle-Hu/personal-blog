package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.constant.BlogUpdateOperation;
import top.huqj.blog.dao.EssayDao;
import top.huqj.blog.model.Blog;
import top.huqj.blog.model.Essay;
import top.huqj.blog.model.ext.MonthAndEssayNum;
import top.huqj.blog.service.IEssayService;
import top.huqj.blog.utils.MarkDownUtil;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static top.huqj.blog.service.impl.BlogServiceImpl.AN_HOUR_MILLIS;

/**
 * @author huqj
 */
@Service("essayService")
@Log4j
public class EssayServiceImpl implements IEssayService {

    @Autowired
    private EssayDao essayDao;

    @Autowired
    private RedisManager redisManager;

    @Value("${redis.hash.essay.brothers}")
    private String essayBrothersHashKey;

    @Value("${redis.list.essay.month2essay.prefix}")
    private String month2essayListKeyPrefix;

    private int TOTAL_ESSAY_NUM = -1;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimpleDateFormat monthDateFormat = new SimpleDateFormat("yyyyMM");

    @PostConstruct
    public void init() {
        TOTAL_ESSAY_NUM = essayDao.count();
        log.info("init total essay num as " + TOTAL_ESSAY_NUM);
        //定期更新
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                TOTAL_ESSAY_NUM = essayDao.count();
                log.info("update total essay num:" + TOTAL_ESSAY_NUM);
            }
        }, AN_HOUR_MILLIS, AN_HOUR_MILLIS);
    }

    @Override
    public Essay findById(int id) {
        List<Essay> essayList = essayDao.findById(id);
        if (essayList.size() == 1) {
            Essay essay = essayList.get(0);
            //设置显示时间
            essay.setPublishTimeStr(dateFormat.format(essay.getPublishTime()));
            essay.setTitle(essay.getTitle().replace("<", "&lt;").replace(">", "&gt;"));
            return essay;
        }
        return null;
    }

    @Override
    public synchronized void insertOne(Essay essay) {
        //如果是md编辑器，则设置纯文本摘要
        if (essay.getType() == BlogConstant.BLOG_TYPE_MD) {
            essay.setText(MarkDownUtil.html2text(essay.getHtmlContent()));
        }
        essayDao.insertOne(essay);
        updateMonth2Essay(essay.getPublishTime(), essay.getId(), BlogUpdateOperation.ADD);
        updateBrotherEssayHash(essay.getId(), BlogUpdateOperation.ADD);
        TOTAL_ESSAY_NUM++;
    }

    /**
     * 更新上下篇文章的关系
     * copy from BlogServiceImpl
     *
     * @param id
     * @param operation
     */
    private void updateBrotherEssayHash(int id, BlogUpdateOperation operation) {
        switch (operation) {
            case ADD: {
                int preBlogId = id - 1;
                while (preBlogId > 0) {
                    Optional<String> preBlogIdValue = redisManager.getHashValueByKey(essayBrothersHashKey, String.valueOf(preBlogId));
                    if (preBlogIdValue.isPresent() && preBlogIdValue.get().endsWith("-0")) {
                        redisManager.setHash(essayBrothersHashKey, String.valueOf(id), preBlogId + "-0");
                        String oldValue = preBlogIdValue.get();
                        redisManager.setHash(essayBrothersHashKey, String.valueOf(preBlogId),
                                oldValue.substring(0, oldValue.indexOf("-")) + "-" + id);
                        break;
                    }
                    preBlogId--;
                }
                if (preBlogId == 0) {
                    log.warn("can not find previous essay, this can be legal only when insert the first essay.");
                    redisManager.setHash(essayBrothersHashKey, String.valueOf(id), "0-0");
                }
                break;
            }
            case DELETE: {
                Optional<String> brotherIds = redisManager.getHashValueByKey(essayBrothersHashKey, String.valueOf(id));
                if (brotherIds.isPresent()) {
                    redisManager.removeHashKey(essayBrothersHashKey, String.valueOf(id));
                    if ("0-0".equals(brotherIds.get())) {
                        break;
                    }
                    String[] brotherIdsValueStr = brotherIds.get().split("-");
                    if (brotherIdsValueStr.length != 2) {
                        log.error("brotherIdsValueStr is an illegal string: " + brotherIdsValueStr);
                        break;
                    }
                    Optional<String> nextId = redisManager.getHashValueByKey(essayBrothersHashKey, brotherIdsValueStr[1]);
                    if (nextId.isPresent()) {
                        String nextIdValue = nextId.get();
                        if (nextIdValue.contains("-")) {
                            nextIdValue = nextIdValue.substring(nextIdValue.indexOf("-") + 1, nextIdValue.length());
                        } else {
                            log.error("illegal brother id string, nextIdValue=" + nextIdValue);
                            break;
                        }
                        redisManager.setHash(essayBrothersHashKey, brotherIdsValueStr[1]
                                , brotherIdsValueStr[0] + "-" + nextIdValue);
                    }
                    Optional<String> preId = redisManager.getHashValueByKey(essayBrothersHashKey, brotherIdsValueStr[0]);
                    if (preId.isPresent()) {
                        String preIdValue = preId.get();
                        if (preIdValue.contains("-")) {
                            preIdValue = preIdValue.substring(0, preIdValue.indexOf("-"));
                        } else {
                            log.error("illegal brother id string, preIdValue=" + preIdValue);
                            break;
                        }
                        redisManager.setHash(essayBrothersHashKey, brotherIdsValueStr[0]
                                , preIdValue + "-" + brotherIdsValueStr[1]);
                    }
                } else {
                    log.error("try to delete a nonexistent essay id. id=" + id);
                }
                break;
            }
        }
    }

    /**
     * 根据操作更新redis中月份和文章的对应关系
     *
     * @param publishTime
     * @param id
     */
    private void updateMonth2Essay(Timestamp publishTime, int id, BlogUpdateOperation op) {
        try {
            String month = monthDateFormat.format(publishTime);
            switch (op) {
                case ADD: {
                    redisManager.addListOnHead(month2essayListKeyPrefix + month, String.valueOf(id));
                    break;
                }
                case DELETE: {
                    List<String> idList = redisManager.getListValues(month2essayListKeyPrefix + month);
                    Iterator<String> iterator = idList.iterator();
                    while (iterator.hasNext()) {
                        String ele = iterator.next();
                        if (ele.equals(String.valueOf(id))) {
                            iterator.remove();
                        }
                    }
                    redisManager.deleteListAllValue(month2essayListKeyPrefix + month);
                    redisManager.addListValueBatch(month2essayListKeyPrefix + month, idList);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("error when update month2essay redis list.", e);
        }
    }

    @Override
    public synchronized void updateOne(Essay essay) {
        essay.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        essayDao.updateOne(essay);
    }

    @Override
    public synchronized void deleteById(int id) {
        //更新redis
        Essay essay = findById(id);
        if (essay == null) {
            log.warn("try to delete an nonexistent essay, id=" + id);
            return;
        }
        updateMonth2Essay(essay.getPublishTime(), id, BlogUpdateOperation.DELETE);
        updateBrotherEssayHash(id, BlogUpdateOperation.DELETE);
        //从数据库中删除
        essayDao.deleteOne(id);
        TOTAL_ESSAY_NUM--;
    }

    @Override
    public List<Essay> findEssayByIdList(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return essayDao.findEssayByIdList(ids);
    }

    @Override
    public synchronized int count() {
        if (TOTAL_ESSAY_NUM == -1) {
            TOTAL_ESSAY_NUM = essayDao.count();
        }
        return TOTAL_ESSAY_NUM;
    }

    @Override
    public int countByMonth(String month) {
        return (int) redisManager.getListLength(month2essayListKeyPrefix + month);
    }

    @Override
    public List<MonthAndEssayNum> getAllMonthAndEssayNum() {
        Set<String> month2EssayKeys = redisManager.listKeysByPrefix(month2essayListKeyPrefix);
        //给月份排个序
        List<String> monthList = new ArrayList<>(month2EssayKeys);
        Collections.sort(monthList);
        List<MonthAndEssayNum> result = new ArrayList<>();
        monthList.forEach(e -> {
            int num = (int) redisManager.getListLength(e);
            if (num > 0) {
                String monthName = e.substring(e.indexOf("-") + 1, e.length());
                if (monthName.length() == 6) {
                    String chMonthStr = parseChineseNameOfMonth(monthName);
                    result.add(new MonthAndEssayNum(chMonthStr, monthName, num));
                }
            }
        });
        return result;
    }

    @Override
    public List<Essay> getEssayListByMonth(String month) {
        if (StringUtils.isEmpty(month)) {
            return Collections.emptyList();
        }
        String monthSuffix = reparseChineseNameOfMonth(month);
        if (monthSuffix.equals(month)) {
            return Collections.emptyList();
        }
        List<String> idList = redisManager.getListValues(month2essayListKeyPrefix + monthSuffix);
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        List<Integer> ids = new ArrayList<>();
        idList.forEach(id -> {
            try {
                ids.add(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                log.error("error when parse id of essay by month.", e);
            }
        });
        return essayDao.findEssayByIdList(ids);
    }

    @Override
    public List<Essay> findLatestEssayListByPageInfo(Map<String, Integer> map) {
        if (map.get(BlogConstant.PAGE_OFFSET) == null || map.get(BlogConstant.PAGE_NUM) == null) {
            return Collections.emptyList();
        }
        List<Essay> essayList = essayDao.findLatestByPageInfo(map);
        postProccess(essayList);
        return essayList;
    }

    @Override
    public List<Essay> findLatestEssayListByPageInfoAndMonth(Map<String, Object> map) {
        if (map.get(BlogConstant.PAGE_OFFSET) == null || map.get(BlogConstant.PAGE_NUM) == null
                || map.get(BlogConstant.TYPE_MONTH) == null) {
            return Collections.emptyList();
        }
        String monthSuffix = (String) map.get(BlogConstant.TYPE_MONTH);
        List<String> monthBlogIds = redisManager.getListValues(month2essayListKeyPrefix + monthSuffix);
        int offset = (Integer) map.get(BlogConstant.PAGE_OFFSET), size = (Integer) map.get(BlogConstant.PAGE_NUM);
        if (CollectionUtils.isEmpty(monthBlogIds) || monthBlogIds.size() <= offset) {
            return Collections.emptyList();
        }
        monthBlogIds = monthBlogIds.subList(offset, Math.min(offset + size, monthBlogIds.size()));
        List<Integer> idList = new ArrayList<>();
        monthBlogIds.forEach(id -> {
            try {
                idList.add(Integer.parseInt(id));
            } catch (NumberFormatException e1) {
                log.error("error once when parse essay id.", e1);
            }
        });
        List<Essay> essayList = essayDao.findEssayByIdList(idList);
        postProccess(essayList);
        return essayList;
    }

    /**
     * 一些公共的处理
     *
     * @param essayList
     */
    private void postProccess(List<Essay> essayList) {
        essayList.forEach(essay -> {
            essay.setPublishTimeStr(dateFormat.format(essay.getPublishTime()));
            essay.setUpdateTimeStr(dateFormat.format(essay.getUpdateTime()));
            essay.setTitle(essay.getTitle().replace("<", "&lt;").replace(">", "&gt;"));
            String pre = essay.getText();
            if (pre.length() > 100) {
                essay.setText(pre.substring(0, 100));
            }
        });
    }

    @Override
    public Essay getPreviousEssay(int id) {
        return getBrother(id, 0);
    }

    @Override
    public Essay getNextEssay(int id) {
        return getBrother(id, 1);
    }

    @Override
    public void scanOnce(int id) {
        essayDao.addScanNum(id);
    }

    /**
     * copy from BlogServiceImpl
     *
     * @param id
     * @param previousOrNext
     * @return
     */
    private Essay getBrother(int id, int previousOrNext) {
        Optional<String> brotherIds = redisManager.getHashValueByKey(essayBrothersHashKey, String.valueOf(id));
        Essay essay = null;
        if (brotherIds.isPresent()) {
            String[] split = brotherIds.get().split("-");
            if (split.length != 2) {
                log.error("the brother ids of {" + id + "} is not equals two.");
            } else {
                try {
                    essay = findById(Integer.parseInt(split[previousOrNext]));
                } catch (Exception e) {
                    log.error("error when parse brother essays' id.", e);
                }
            }
        }
        return essay;
    }

    /**
     * 将数字形式的月份转换成中文格式
     *
     * @param month
     * @return
     */
    private String parseChineseNameOfMonth(String month) {
        if (month.charAt(4) == '0') {
            return month.substring(0, 4) + "年" + month.charAt(5) + "月";
        } else {
            return month.substring(0, 4) + "年" + month.substring(4, 6) + "月";
        }
    }

    /**
     * 将中文格式的月份转换成数字形式的字符串
     *
     * @param month
     * @return
     */
    private String reparseChineseNameOfMonth(String month) {
        String result = month = month.replace("年", "").replace("月", "");
        if (result.length() == 5) {  //月份小于10的情况,例如 20189
            result = result.substring(0, 4) + "0" + result.charAt(4);
        }
        if (result.length() != 6) {
            log.error("error when reparse chinese month string to num format. result=" + result);
            return month;  //返回原格式
        }
        return result;
    }

}
