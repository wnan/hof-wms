package com.hof.wms.integration.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * SpEL表达式求值工具类
 * 用于解析任务配置中的表达式参数，如日期表达式
 */
@Slf4j
public class SpelExpressionUtil {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public static Object evaluate(String expression) {
        return evaluate(expression, null);
    }

    public static Object evaluate(String expression, Map<String, Object> variables) {
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }
            return PARSER.parseExpression(expression).getValue(context);
        } catch (Exception e) {
            log.error("SpEL表达式求值失败: {}", expression, e);
            return null;
        }
    }

    public static String evaluateAsString(String expression) {
        Object result = evaluate(expression);
        return result != null ? result.toString() : null;
    }

    public static String evaluateAsString(String expression, String defaultValue) {
        String result = evaluateAsString(expression);
        return result != null ? result : defaultValue;
    }

    public static boolean isExpression(String value) {
        return value != null && (value.startsWith("T(") || value.startsWith("#"));
    }
}
