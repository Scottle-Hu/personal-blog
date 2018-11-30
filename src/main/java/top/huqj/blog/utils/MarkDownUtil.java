package top.huqj.blog.utils;

/**
 * markdown语法转换工具
 *
 * @author huqj
 */
public class MarkDownUtil {

    /**
     * 将md语法转换为html页面
     *
     * @param md md原始文本
     * @return html标签
     */
    public static String md2html(String md) {
        return null;
    }

    /**
     * 提取忽略md语法标签的纯文本内容
     *
     * @param md
     * @return
     */
    public static String md2text(String md) {
        return null;
    }

    /**
     * 忽略html标签的出文本内容
     *
     * @param html
     * @return
     */
    public static String html2text(String html) {
        int indexStart = html.indexOf("<");
        int indexEnd = html.indexOf(">");
        while (indexStart != -1 && indexEnd != -1) {
            html = html.replace(html.substring(indexStart, indexEnd + 1), "");
            indexStart = html.indexOf("<");
            indexEnd = html.indexOf(">");
        }
        return html;
    }

}
