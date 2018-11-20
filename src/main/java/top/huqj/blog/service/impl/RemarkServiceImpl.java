package top.huqj.blog.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.huqj.blog.dao.RemarkDao;
import top.huqj.blog.model.Remark;
import top.huqj.blog.service.IRemarkService;

import java.util.List;

/**
 * @author huqj
 */
@Log4j
@Service("remarkService")
public class RemarkServiceImpl implements IRemarkService {

    @Autowired
    private RemarkDao remarkDao;

    @Override
    public Remark findById(int id) {
        List<Remark> remarkList = remarkDao.findById(id);
        if (remarkList.isEmpty() || remarkList.size() > 1) {
            log.warn("one id find 0 or many records. id=" + id);
            return null;
        }
        return remarkList.get(0);
    }

    @Override
    public List<Remark> findByArticleId(int articleId) {
        return remarkDao.findByArticleId(articleId);
    }

    @Override
    public void insert(Remark remark) {
        remarkDao.insertOne(remark);
    }

    @Override
    public int countByArticleId(int articleId) {
        return remarkDao.countByArticleId(articleId);
    }

    @Override
    public void deleteById(int id) {
        remarkDao.deleteById(id);
    }

}
