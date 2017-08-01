package org.yunshanmc.lmc.core.command;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SimpleCommand {

    String name() default "";

    String[] permissions() default {};

    String description() default " ";

    String[] aliases() default {};

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface Sender {}

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface OptionalStart{}
}
