package org.yunshanmc.lmc.core.command;

import com.google.common.base.Joiner;

import java.lang.annotation.*;

/**
 * @author Yun-Shan
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SimpleCommand {

    /**
     * 命令名
     *
     * @return 命令名
     */
    String name();

    /**
     * 命令权限
     * <p>
     * 留空视为无需任何权限即可执行<br>
     * 以"<code>.</code>"开头则自动在最前面加上主命令名<br>
     * 其它情况视为完整权限名
     *
     * @return 命令权限
     */
    String[] permissions() default {};

    /**
     * 命令语法
     * <p>
     * 用于在命令帮助中显示，顺序列出参数即可，无需在前面加命令名；<p>
     * 以#开头的作为语言文件的msgKey(去掉开头#)查找描述；<br>
     * 若留空则以<code>command.usage.命令名({@link #name()}的值)</code>作为语言文件的msgKey(去掉开头#)查找用法
     * (若留空且语言文件中无法找到用法，则根据方法的参数列表自动生成用法)
     *
     * @return 命令语法
     */
    String usage() default "";

    /**
     * 命令描述
     * <p>
     * 留空则以<code>command.description.命令名({@link #name()}的值)</code>作为语言文件的msgKey(去掉开头#)查找描述；<br>
     * 以#开头的作为语言文件的msgKey(去掉开头#)查找描述；<br>
     * 其它情况直接作为描述使用
     *
     * @return 命令描述
     */
    String description() default "";

    /**
     * 命令别名
     *
     * @return 命令别名
     */
    String[] aliases() default {};

    /**
     * 是否给命令增加默认权限
     * <p>
     * 权限格式为: 主命令.子命令<br>
     * 比如主命令为lmc，子命令为reload，默认权限即为lmc.reload
     *
     * @return 是否给命令增加默认权限
     */
    boolean useDefaultPermission() default false;

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

    /**
     * 用户输入的命令的原始信息，只能有一个
     * <p>
     * 注解的参数必须是String类型
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface RawInfo{}

    class CommandRawInfo {
        private final String label;
        private final String name;
        private final String[] argsWithCmdName;

        // 由于用到RawInfo的情况很少，所以不用预先初始化，用到的时候再初始化
        private String cmdLine;
        private String[] args;

        public CommandRawInfo(String label, String name, String[] argsWithCmdName) {
            this.label = label;
            this.name = name;
            this.argsWithCmdName = argsWithCmdName;
        }

        public String getLabel() {
            return this.label;
        }

        public String[] getArgs() {
            if (this.args == null) {
                int nilIdx = -1;
                for (int i = 0; i < argsWithCmdName.length; i++) {
                    if (argsWithCmdName[i] == null) {
                        nilIdx = i;
                        break;
                    }
                }
                int len = nilIdx >= 0 ? nilIdx : argsWithCmdName.length;
                String[] args = new String[len + 1];
                args[0] = this.name;
                System.arraycopy(argsWithCmdName, 0, args, 1, len);
                this.args = args;
            }
            return this.args;
        }

        public String getCmdLine() {
            if (this.cmdLine == null) {
                this.cmdLine = '/' + label + ' ' + Joiner.on(' ').join(this.getArgs());
            }
            return this.cmdLine;
        }
    }
}
