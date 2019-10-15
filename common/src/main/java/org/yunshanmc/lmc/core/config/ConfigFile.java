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
public @interface ConfigFile {

    /**
     * 同 path ，读取配置时将优先使用path，如果未设置path则使用value
     *
     * @see #path()
     */
    String value() default "";


    /**
     * 当注解在类上时，值应当为配置文件路径，如config.yml，settings.yml等；如有多级目录，目录分隔符使用/或\皆可
     * <ul><li>
     *     如果配置文件是config.yml，实际上不需要在类上注释。因为当类上没有注解的时候会默认使用config.yml作为配置文件
     * </li></ul>
     *
     * @return 配置路径，不能为空
     */
    String path() default "";

    /**
     * 若true，则当文件不存在时会抛出异常
     * <br>
     * 若false(默认为false)，则文件不存在时仅使用默认值初始化并正常返回
     *
     * @return 配置文件是否必须存在
     */
    boolean needFileExists() default false;

    /**
     * 如果该类的配置不在文件的根节点，可以定义所在的子节点
     *
     * @return 配置节点
     */
    String section() default "";
    // TODO 考虑要不要加一个把所有字段都加入配置的属性，这样就不用每个字段都加，但是具体是否有必要还要再斟酌
}
