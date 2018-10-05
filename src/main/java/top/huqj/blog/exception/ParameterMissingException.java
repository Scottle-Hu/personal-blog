package top.huqj.blog.exception;

/**
 * 必要参数缺失时抛出的异常
 *
 * @author huqj
 */
public class ParameterMissingException extends Exception {

    public ParameterMissingException(String message) {
        super(message);
    }

}
