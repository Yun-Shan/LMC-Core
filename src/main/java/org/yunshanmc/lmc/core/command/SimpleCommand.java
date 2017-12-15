package org.yunshanmc.lmc.core.command;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SimpleCommand {

    /**
     * 命令名
     */
    String name() default "";
    /**
     * 命令名(便于使用故value方法也可用于命令名)
     * <p>
     * 当name()和value()都有值且不相等时，name()优先
     */
    String value() default "";

    /**
     * 命令权限
     * <p>
     * 以"<code>.</code>"开头且插件名由英文/数字/下划线/连字符组成，则自动在最前面加上小写插件名。<br>
     * 其它情况视为完整权限名。
     */
    String[] permissions() default {};

    /**
     * 命令描述
     * <p>
     * 以#开头的作为语言文件的msgKey(去掉开头#)查找描述，其它情况直接使用
     */
    String description() default "";

    /**
     * 命令别名
     */
    String[] aliases() default {};

    /**
     * 命令发送者，只能有一个
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface Sender {}

    /**
     * 可选参数起始点，只能有一个
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface OptionalStart{}
}
