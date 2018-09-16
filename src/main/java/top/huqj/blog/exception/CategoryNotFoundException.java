package top.huqj.blog.exception;

/**
 * 当类别在数据库中不存在时抛出的异常
 *
 * @author huqj
 */
public class CategoryNotFoundException extends Exception {

    public CategoryNotFoundException(String message) {
        super(message);
    }

}
