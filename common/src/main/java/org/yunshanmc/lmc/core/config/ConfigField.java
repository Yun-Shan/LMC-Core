package org.yunshanmc.lmc.core.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识配置文件
 *
 * @author Yun-Shan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface ConfigField {

    /**
     * 同 path
     * <p>
     * 读取配置时将优先使用path
     * <br>
     * 如果未设置path则使用value
     * <br>如果value也未设置则使用字段名，当且仅当使用字段名时，会先查询是否有字段名对应的配置值，
     * 若没有则将字段名从驼峰形式转为小写中划线形式再尝试一次
     *
     * @see #path()
     */
    String value() default "";


    /**
     * @return 配置中key的路径，不能为空
     */
    String path() default "";

    /**
     * 字段默认值
     *
     * @return 默认值，留空时视为无默认值
     */
    String defaultValue() default "";

    /**
     * 该选项仅用于String类型的配置，因为注解参数无法为null，故加此参数。
     * 若希望String类型的配置默认值是null而非空字符串则可将此参数设为true，否则设为false
     * <br>
     * 默认为true
     *
     * @return 是否禁用默认值
     */
    boolean defaultValueNull() default true;
}
