package top.huqj.blog.service;

/**
 * 提供随笔id并且计数
 *
 * @author huqj
 */
public interface IEssayIdProvider {

    int provideId();

    int currentMaxId();

}
