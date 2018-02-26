package org.yunshanmc.lmc.core.command;

import com.google.common.base.Strings;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.bukkit.LMCBukkitPlugin;
import org.yunshanmc.lmc.core.bukkit.command.executors.BukkitExecutor;
import org.yunshanmc.lmc.core.bungee.LMCBungeeCordPlugin;
import org.yunshanmc.lmc.core.bungee.command.executors.BungeeCordExecutor;
import org.yunshanmc.lmc.core.command.executors.CommandExecutor;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.Message;
import org.yunshanmc.lmc.core.message.MessageManager;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class DefaultCommandManager implements CommandManager {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final String handleCommand;

    private CommandExecutor commandExecutor;

    private MessageManager messageManager;
    private MessageSender messageSender;

    public DefaultCommandManager(LMCPlugin plugin, String handleCommand) {
        this(plugin, handleCommand, null, (String[]) null);
    }

    /**
     * 此方法是给BungeeCord插件使用的，Bukkit插件传入的<code>permission, aliases</code>参数将被忽略<br>
     * Bukkit插件建议使用{@link #DefaultCommandManager(LMCPlugin, String)}
     */
    public DefaultCommandManager(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        this.handleCommand = handleCommand;
        this.messageManager = plugin.getMessageManager();
        this.messageSender = this.messageManager.getMessageSender();

        if (PlatformUtils.isBukkit() && plugin instanceof LMCBukkitPlugin) {
            this.commandExecutor = new BukkitExecutor(this, plugin.getMessageManager().getMessageSender(),
                                                      (LMCBukkitPlugin) plugin);
        } else if (PlatformUtils.isBungeeCord() && plugin instanceof LMCBungeeCordPlugin) {
            this.commandExecutor = new BungeeCordExecutor(this, plugin.getMessageManager().getMessageSender(),
                                                          // Java抽风，不能转LMCBungeeCordPlugin，只能转Plugin，鬼知道为什么
                                                          (Plugin) plugin, permission, aliases);
        }
    }

    @Override
    public String getHandleCommand() {
        return this.handleCommand;
    }

    @Override
    public void registerCommand(LMCCommand command) {
        this.commandExecutor.registerCommand(command);
    }

    @Override
    public void registerCommands(SimpleLMCCommand command) {
        String msgPath = "simplecommand.register.";
        Arrays.stream(command.getClass().getDeclaredMethods()).filter(
                m -> m.isAnnotationPresent(SimpleCommand.class)).forEach(m -> {

            SimpleCommand cmdAnno = m.getAnnotation(SimpleCommand.class);
            try {
                Parameter[] parameters = m.getParameters();
                int paramCount = parameters.length;
                int senderIdx = -1;
                int optionalStartIds = -1;
                int rawInfoIdx = -1;

                for (int i = 0; i < paramCount; i++) {

                    Parameter param = parameters[i];
                    if (param.isAnnotationPresent(SimpleCommand.Sender.class)) {
                        if (senderIdx != -1) {
                            // 多个Sender参数无意义，禁止
                            this.messageSender.warningConsole(msgPath + "fail.tooManySenders",
                                                              this.handleCommand,
                                                              cmdAnno.name());
                            return;
                        }

                        boolean allow = false;
                        if (PlatformUtils.isTest()) {
                            allow = true;
                        }
                        if (!allow && PlatformUtils.isBukkit()) {
                            allow = CommandSender.class.isAssignableFrom(param.getType());
                        }
                        if (!allow && PlatformUtils.isBungeeCord()) {
                            allow = net.md_5.bungee.api.CommandSender.class.isAssignableFrom(param.getType());
                        }
                        if (!allow) {
                            // Sender参数类型必须是CommandSender/子接口/实现类
                            this.messageSender.warningConsole(msgPath + "fail.unsupportedSender",
                                                              this.handleCommand,
                                                              cmdAnno.name());
                            return;
                        }
                        senderIdx = i;
                    }
                    if (param.isAnnotationPresent(SimpleCommand.OptionalStart.class)) {
                        if (optionalStartIds != -1) {
                            // 多个OptionalStart无意义，禁止
                            this.messageSender.warningConsole(msgPath + "fail.tooManyOptionalStart",
                                                              this.handleCommand,
                                                              cmdAnno.name());
                            return;
                        }
                        optionalStartIds = i;
                    }
                    if (param.isAnnotationPresent(SimpleCommand.RawInfo.class)) {
                        if (rawInfoIdx != -1) {
                            // 多个RawInfo无意义，禁止
                            this.messageSender.warningConsole(msgPath + "fail.tooManyRawInfo",
                                                              this.handleCommand,
                                                              cmdAnno.name());
                            return;
                        }
                        if (!param.getType().equals(SimpleCommand.CommandRawInfo.class)) {
                            // 必须是CommandRawInfo类型
                            this.messageSender.warningConsole(msgPath + "fail.InvalidRawInfo",
                                                              this.handleCommand,
                                                              cmdAnno.name());
                            return;
                        }
                        rawInfoIdx = i;
                    }
                }

                MethodHandle handle;
                try {
                    m.setAccessible(true);
                    handle = LOOKUP.unreflect(m);
                } catch (IllegalAccessException e) {// 已经setAccessible，该异常不会出现
                    e.printStackTrace();
                    throw new UnknownError();
                }

                boolean needSender = senderIdx != -1;
                boolean needRawInfo = rawInfoIdx != -1;
                // 获取参数转换器
                int convertersLen = paramCount;
                if (needSender) convertersLen -= 1;
                if (needRawInfo) convertersLen -= 1;
                MethodHandle[] converters = new MethodHandle[convertersLen];
                for (int i = 0; i < paramCount; i++) {
                    if ((needSender && i == senderIdx) || (needRawInfo && i == rawInfoIdx)) continue;
                    ParameterConverter<?> converter = ParameterConverter.getConverter(parameters[i].getType());
                    if (converter == null) {
                        // 未注册相应类型的参数转换器
                        this.messageSender.warningConsole(msgPath + "fail.cantFoundParamConverter",
                                                          this.handleCommand,
                                                          cmdAnno.name(),
                                                          parameters[i].getType().getName());
                        return;
                    }

                    int idx = i;
                    if (needSender && i > senderIdx) idx -= 1;
                    if (needRawInfo && i > rawInfoIdx) idx -= 1;
                    converters[idx] = converter.toMethodHandle();
                }

                // 绑定到原始对象
                // 必须最先绑定，后续处理都忽略了此方法句柄的this参数
                handle = handle.bindTo(command);

                if (needRawInfo || needSender) {// 移动参数位置
                    Class<?>[] args = m.getParameterTypes();
                    Class<?> senderType = null;
                    if (needSender) senderType = args[senderIdx];
                    Class<?> rawInfoType = null;
                    if (needRawInfo) rawInfoType = args[rawInfoIdx];

                    int right = Math.max(rawInfoIdx, senderIdx);
                    int left = Math.min(rawInfoIdx, senderIdx);

                    if (left >= 0 && args.length > 2) {
                        System.arraycopy(args, left, args, left + 1, right - left);
                        if (left > 0) System.arraycopy(args, 0, args, 2, left);
                        else if (right > 1) args[2] = args[1];
                    } else if (args.length > 1) {
                        System.arraycopy(args, 0, args, 1, right);
                    }

                    if (needSender) {
                        args[0] = senderType;
                        if (needRawInfo) args[1] = rawInfoType;
                    } else {
                        args[0] = rawInfoType;
                    }

                    int[] idxs = new int[args.length];
                    /*
                     * 将senderIdx移到第一个参数，senderIdx前面的参数依次右移
                     * 故senderIdx处的idx为0，senderIDx前面的索引都+1
                     * 该处索引序列转换示例
                     *         4
                     * 0 1 2 3 s 5 6
                     *         5
                     * 1 2 3 4 s 5 6
                     *
                     * 1 2 3 4 0 5 6
                     *
                     * MethodHandles.permuteArguments的reorder语义：原来的参数索引在新排序中的位置(而非新排序在原索引的位置)
                     */
                    for (int i = right + 1; i < idxs.length; i++) {
                        idxs[i] = i;
                    }
                    for (int i = left + 1; i < right; i++) {
                        idxs[i] = i + 1;
                    }
                    if (left >= 0) {
                        for (int i = 0; i < left; i++) {
                            idxs[i] = i + 2;
                        }
                        idxs[senderIdx] = 0;
                        idxs[rawInfoIdx] = 1;
                        handle = MethodHandles.permuteArguments(handle, MethodType.methodType(m.getReturnType(), args),
                                                                idxs);
                    } else {
                        if (needSender) {
                            idxs[senderIdx] = 0;
                            handle = MethodHandles.permuteArguments(handle,
                                                                    MethodType.methodType(m.getReturnType(), args),
                                                                    idxs);
                            handle = MethodHandles.dropArguments(handle, 1, SimpleCommand.CommandRawInfo.class);
                        } else {
                            idxs[rawInfoIdx] = 0;
                            handle = MethodHandles.permuteArguments(handle,
                                                                    MethodType.methodType(m.getReturnType(), args),
                                                                    idxs);
                            handle = MethodHandles.dropArguments(handle, 0, CommandSender.class);
                        }
                    }
                } else {// 没有Sender和CommandRawInfo参数的在最前面加上Sender和CommandRawInfo参数
                    handle = MethodHandles.dropArguments(handle, 0, CommandSender.class,
                                                         SimpleCommand.CommandRawInfo.class);
                }

                // 过滤参数
                handle = MethodHandles.filterArguments(handle, 2, converters);
                // 字符串数组转为参数列表
                handle = handle.asSpreader(String[].class, converters.length);

                // 参数最大数量，忽略Sender和RawInfo参数
                int maxArgCount = paramCount;
                if (needSender) maxArgCount -= 1;
                if (needRawInfo) maxArgCount -= 1;

                // 参数最小数量，有可选参数时是optionalStartIds，无可选参数时是最大参数数量
                int minArgCount = optionalStartIds > 0 ? optionalStartIds : paramCount;
                // 没有可选参数，或者Sender参数的位置在optionalStartIds前面时，忽略Sender参数
                if (needSender && (optionalStartIds <= 0 || senderIdx < optionalStartIds))
                    minArgCount -= 1;
                // 没有可选参数，或者RawInfo参数的位置在optionalStartIds前面时，忽略RawInfo参数
                if (needRawInfo && (optionalStartIds <= 0 || rawInfoIdx < optionalStartIds))
                    minArgCount -= 1;

                SimpleCommandInfo cmdInfo = new SimpleCommandInfo();
                cmdInfo.setName(cmdAnno.name());
                cmdInfo.setAliases(cmdAnno.aliases());
                cmdInfo.setPermissions(cmdAnno.permissions());
                if (Strings.isNullOrEmpty(cmdAnno.usage())) {
                    // TODO 找不到时自动生成
                    String path = "command.usage." + cmdAnno.name();
                    if (this.messageManager.getMessage(path) instanceof Message.MissingMessage) {
                        StringBuilder msg = new StringBuilder();
                        for (int i = 0; i < parameters.length; i++) {
                            if (i == senderIdx || i == rawInfoIdx) continue;
                            if (i >= optionalStartIds) msg.append('[');
                            else msg.append('<');
                            msg.append(parameters[i].getName());
                            if (i >= optionalStartIds) msg.append(']');
                            else msg.append('>');
                            msg.append(' ');
                        }
                        if (msg.length() > 0) {
                            cmdInfo.setUsage(msg.deleteCharAt(msg.length() - 1).toString());
                        } else {
                            cmdInfo.setUsage("");
                        }
                    } else {
                        cmdInfo.setUsage(this.messageSender.getMessage(path));
                    }
                } else {
                    cmdInfo.setUsage(cmdAnno.usage());
                }
                if (Strings.isNullOrEmpty(cmdAnno.description())) {
                    cmdInfo.setDescription(this.messageSender.getMessage("command.description." + cmdAnno.name()));
                } else {
                    if (cmdAnno.description().charAt(0) == '#' && cmdAnno.description().length() > 1) {
                        cmdInfo.setDescription(this.messageSender.getMessage(cmdAnno.description().substring(1)));
                    } else {
                        cmdInfo.setDescription(cmdAnno.description());
                    }
                }
                this.registerCommand(new SimpleCommandImpl(cmdInfo, this.messageSender, handle, m,
                                                           needSender ? parameters[senderIdx].getType() : null,
                                                           minArgCount, maxArgCount));
            } catch (Throwable t) {
                ExceptionHandler.handle(t);
                this.messageSender.warningConsole("bug.SimpleCommandRegister", cmdAnno.name(),
                                                  m.getDeclaringClass(), m.toGenericString());
            }
        });
    }

    @Override
    public void unregisterCommand(String cmdName) {
        this.commandExecutor.unregisterCommand(cmdName);
    }

    @Override
    public List<LMCCommand> getCommands() {
        return this.commandExecutor.getCommands();
    }

    static class SimpleCommandImpl extends LMCCommand {

        private final MethodHandle handle;
        private final Method rawMethod;
        private final Class<?> senderType;
        private final int minArgCount;
        private final int maxArgCount;

        private MessageSender messageSender;

        // TODO cmdInfo 信息读取 命令执行时进行相应处理
        private SimpleCommandImpl(SimpleCommandInfo cmdInfo, MessageSender messageSender, MethodHandle handle,
                                  Method rawMethod, Class<?> senderType, int minArgCount, int maxArgCount) {
            super(cmdInfo.getName(), cmdInfo.getUsage(),
                  cmdInfo.getDescription(), cmdInfo.getAliases(), cmdInfo.getPermissions());

            this.messageSender = messageSender;

            this.handle = handle;
            this.rawMethod = rawMethod;
            this.senderType = senderType;
            this.minArgCount = minArgCount;
            this.maxArgCount = maxArgCount;
        }

        @Override
        public void execute(CommandSender sender, String label, String... args) {// TODO 提示
            this.execute((Object) sender, label, args);
        }

        @Override
        public void showHelp(CommandSender sender) {

        }

        @Override
        public void execute(net.md_5.bungee.api.CommandSender sender, String label, String... args) {
            this.execute((Object) sender, label, args);
        }

        private void execute(Object sender, String label, String... args) {// TODO 提示
            if (this.senderType != null && !this.senderType.isInstance(sender)) {
                this.failTip(sender, "command.simpleCommand.senderTypeRequire."
                        + this.senderType.getName().replace('.', '-'));
                return;
            } else if (args.length > this.maxArgCount) {
                this.failTip(sender, "command.simpleCommand.TooManyArgs");
                return;
            } else if (args.length < this.minArgCount) {
                this.failTip(sender, "command.simpleCommand.TooLittleArgs");
                return;
            }
            if (args.length < this.maxArgCount) {
                args = Arrays.copyOf(args, this.maxArgCount);
            }
            try {
                this.handle.invoke(sender, new SimpleCommand.CommandRawInfo(label, args), args);
            } catch (ParamConverterFailException e) {
                this.failTip(sender, "command.simpleCommand.ConvertFail", e.getArg());
            } catch (WrongMethodTypeException | ClassCastException e) {
                // 该项错误不应该出现，若出现则是DefaultCommandManager类的registerCommands(SimpleLMCCommand command)方法有bug
                ExceptionHandler.handle(e);
                this.messageSender.warningConsole("bug.SimpleCommandRegister",
                                                  e.getClass().getName(), e.getMessage(),
                                                  this.getName(),
                                                  this.rawMethod.getDeclaringClass(), this.rawMethod.toGenericString());
                this.failTip(sender, "command.simpleCommand.SimpleCommandBug");
            } catch (Throwable e) {// 该项为命令处理方法抛出的异常
                if (e instanceof Error) {
                    throw (Error) e;// Error是程序无法处理的，不应掩盖，故原样抛出
                }
                ExceptionHandler.handle(e);
                this.failTip(sender, "command.simpleCommand.DeveloperCommandError", this.messageSender.getMessage("message.name"));
            }
        }

        private void failTip(Object sender, String msgKey, Object... args) {
            if (PlatformUtils.isBukkit() && sender instanceof CommandSender) {
                this.messageSender.warning((CommandSender) sender, msgKey, args);
            } else if (PlatformUtils.isBungeeCord() && sender instanceof net.md_5.bungee.api.CommandSender) {
                this.messageSender.warning((net.md_5.bungee.api.CommandSender) sender, msgKey, args);
            }
        }
    }

    static class SimpleCommandInfo {

        private String name;
        private String[] aliases;
        private String usage;
        private String description;
        private String[] permissions;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String[] getAliases() {
            return this.aliases;
        }

        public void setAliases(String[] aliases) {
            this.aliases = aliases;
        }

        public String getUsage() {
            return this.usage;
        }

        public void setUsage(String usage) {
            this.usage = usage;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String[] getPermissions() {
            return this.permissions;
        }

        public void setPermissions(String[] permissions) {
            this.permissions = permissions;
        }
    }
}
