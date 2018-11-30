package top.huqj.blog.service.lucene;

import lombok.extern.log4j.Log4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;
import top.huqj.blog.constant.BlogConstant;
import top.huqj.blog.model.Blog;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;

/**
 * lucene索引增删改入口
 *
 * @author huqj
 */
@Service("luceneIndexer")
@Log4j
public class Indexer {

    @Value("${lucene.indexDir}")
    private String indexDir;

    private IndexWriter indexWriter;

    @PostConstruct
    private void init() {
        try {
            Directory directory = FSDirectory.open(new File(indexDir));
            indexWriter = new IndexWriter(directory,
                    new IndexWriterConfig(Version.LUCENE_40, new IKAnalyzer()));
            indexWriter.commit();  //如果没有这个，在索引库为空的情况下会抛异常
        } catch (Exception e) {
            log.error("lucene error init indexWriter.", e);
        }
    }

    /**
     * 添加一篇博客到lucene
     *
     * @param blog
     */
    public void addDocumentIndex(Blog blog) {
        try {
            Document document = new Document();
            document.add(new TextField(BlogConstant.LUCENE_ID, String.valueOf(blog.getId()), Field.Store.YES));
            document.add(new TextField(BlogConstant.LUCENE_CONTENT,
                    blog.getTitle() + blog.getText(), Field.Store.NO));
            indexWriter.addDocument(document);
            indexWriter.commit();  //提交修改
        } catch (IOException e) {
            log.error("error add new blog document to lucene.blog id=" + blog.getId(), e);
        }
    }

    /**
     * 根据博客id从lucene中删除索引
     *
     * @param id
     */
    public void removeDocumentIndex(int id) {
        try {
            indexWriter.deleteDocuments(new Term(BlogConstant.LUCENE_ID, String.valueOf(id)));
            indexWriter.commit();
        } catch (Exception e) {
            log.error("error remove lucene index by blog id. blog id=" + id, e);
        }
    }

    /**
     * 修改博客的索引
     *
     * @param blog
     */
    public void updateDocumentIndex(Blog blog) {
        try {
            Document document = new Document();
            document.add(new TextField(BlogConstant.LUCENE_ID, String.valueOf(blog.getId()), Field.Store.YES));
            document.add(new TextField(BlogConstant.LUCENE_CONTENT,
                    blog.getTitle() + blog.getText(), Field.Store.NO));
            indexWriter.updateDocument(new Term(BlogConstant.LUCENE_ID, String.valueOf(blog.getId())), document);
            indexWriter.commit();
        } catch (Exception e) {
            log.error("error update lucene index, blog id=" + blog.getId(), e);
        }
    }

    public void close() {
        try {
            indexWriter.close();
        } catch (IOException e) {
            log.error("error close index writer.", e);
        }
    }

    @PreDestroy
    public void destroy() {
        close();
    }

}
