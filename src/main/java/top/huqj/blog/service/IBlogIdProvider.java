package top.huqj.blog.service;

/**
 * 提供博客id并且计数
 *
 * @author huqj
 */
public interface IBlogIdProvider {

    int provideId();

    int currentMaxId();

}
