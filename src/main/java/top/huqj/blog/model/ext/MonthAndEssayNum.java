package top.huqj.blog.model.ext;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huqj
 */
@Data
@AllArgsConstructor
public class MonthAndEssayNum {

    /**
     * 形如 201810
     */
    private String month;

    private String primitiveMonthStr;

    private int num;

}
