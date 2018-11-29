package top.huqj.blog.service.lucene;

import lombok.extern.log4j.Log4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.model.Blog;
import top.huqj.blog.service.IBlogService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * lucene搜索入口
 *
 * @author huqj
 */
@Service("luceneSearcher")
@Log4j
public class Searcher {

    @Autowired
    private IBlogService blogService;

    @Value("${lucene.indexDir}")
    private String indexDir;

    private IndexSearcher searcher;

    private QueryParser queryParser;

    private Directory directory;

    /**
     * 更新索引的时间间隔，也就是修改索引后的生效时间
     */
    private long interval = 1000 * 60;

    @PostConstruct
    public void init() {
        try {
            directory = FSDirectory.open(new File(indexDir));
            searcher = new IndexSearcher(IndexReader.open(directory));
            queryParser = new QueryParser(Version.LUCENE_40,
                    BlogConstant.LUCENE_CONTENT, new StandardAnalyzer(Version.LUCENE_40));
        } catch (Exception e) {
            log.error("error init lucene indexSearcher.", e);
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    searcher = new IndexSearcher(IndexReader.open(directory));
                } catch (IOException e) {
                    log.error("error update lucene indexSearcher.", e);
                }
            }
        }, interval, interval);
    }

    /**
     * 通过查询式返回最相关的若干博客列表(按相关性降序)
     *
     * @param keywords
     * @return
     */
    public List<Blog> topBlogIds(String keywords) {
        try {
            log.info("lucene start search. query=" + keywords);
            long start = System.currentTimeMillis();
            TopDocs topDocs = searcher.search(queryParser.parse(keywords), BlogConstant.LUCENE_MAX_RESULT_NUM);
            log.info("lucene search complete! cost " + (System.currentTimeMillis() - start) + "ms.");
            List<Blog> blogs = new ArrayList<>();
            for (ScoreDoc doc : topDocs.scoreDocs) {
                Document document = searcher.doc(doc.doc);
                int id = Integer.parseInt(document.get(BlogConstant.LUCENE_ID));
                Blog blog = blogService.findBlogById(id);
                if (blog != null) {
                    blogs.add(blog);
                } else {
                    log.warn("search deleted blog, id=" + id);
                }
            }
            if (blogs.isEmpty()) {
                log.warn("empty result! query=" + keywords);
            }
            return blogs;
        } catch (Exception e) {
            log.error("lucene error search. keywords=" + keywords, e);
        }
        return Collections.emptyList();
    }

}
