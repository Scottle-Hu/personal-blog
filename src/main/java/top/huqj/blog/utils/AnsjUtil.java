package top.huqj.blog.utils;

import lombok.extern.log4j.Log4j;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 分词封装
 *
 * @author huqj
 */
@Log4j
public class AnsjUtil {

    /**
     * 传入字符串，分成单词集合
     *
     * @param text
     * @return
     */
    public static Set<String> segment(String text) {
        try {
            Set<String> set = new HashSet<>();
            Result result = DicAnalysis.parse(text);
            for (Term term : result.getTerms()) {
                set.add(term.getName());
            }
            return set;
        } catch (Exception e) {
            log.error("error when ansj segment.", e);
        }
        return Collections.emptySet();
    }

}
