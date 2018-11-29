package top.huqj.blog.constant;

/**
 * 博客相关常量
 *
 * @author huqj
 */
public class BlogConstant {

    /**
     * 以markdown形式记录博客内容
     */
    public static int BLOG_TYPE_MD = 0;

    /**
     * 使用ue编辑器以html格式保存博客内容
     */
    public static int BLOG_TYPE_HTML = 1;

    public static String PAGE_OFFSET = "pageOffset";

    public static String PAGE_NUM = "pageNum";

    /**
     * 按照博客类别或者博客和随笔的时间查找文章
     */
    public static String TYPE_CATEGORY = "category";
    public static String TYPE_MONTH = "month";

    public static String BLOG_TYPE = "blog";
    public static int BLOG_TYPE_ID = 0;

    public static String ESSAY_TYPE = "essay";
    public static int ESSAY_TYPE_ID = 1;

    public static String ACCOUNT_TYPE = "account";

    public static String OAUTH_SESSION_ID = "_huqj_oauth_session_id_";

    /**
     * lucene field相关
     */
    public static String LUCENE_ID = "id";
    public static String LUCENE_CONTENT = "content";

    /**
     * lucene搜索结果最大个数
     */
    public static int LUCENE_MAX_RESULT_NUM = 15;

}
