package org.yunshanmc.lmc.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.yunshanmc.lmc.core.LMCBukkitPlugin;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultCommandManager implements CommandManager, CommandExecutor, TabCompleter {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final String handleCommand;

    private Map<String, LMCCommand> commands = new HashMap<>();

    private MessageSender messageSender;

    public DefaultCommandManager(LMCBukkitPlugin plugin, String handleCommand) {
        this.handleCommand = handleCommand;
        PluginCommand command = plugin.getCommand(handleCommand);
        command.setExecutor(this);
        command.setTabCompleter(this);

        this.messageSender = plugin.getMessageManager().getMessageSender();
    }

    // 测试用
    DefaultCommandManager() {
        Object obj = new Throwable().getStackTrace();
        // 只有测试类能调用
        if ("org/yunshanmc/lmc/core/command/DefaultCommandManagerTest".equals(
                new Throwable().getStackTrace()[1].getClassName()))
            throw new UnsupportedOperationException();
        this.handleCommand = "test";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> cmds = new ArrayList<>(this.commands.keySet());
        if (args.length == 0) return cmds;

        cmds.removeIf(cmd -> cmd.startsWith(args[0]));
        return cmds;
    }

    @Override
    public String getHandleCommand() {
        return this.handleCommand;
    }

    @Override
    public void registerCommand(LMCCommand command) {
        this.commands.put(command.getName(), command);
    }

    @Override
    public void registerCommands(SimpleLMCCommand command) {
        String msgPath = "simplecommand.register.";
        Arrays.stream(command.getClass().getDeclaredMethods()).filter(
                m -> m.isAnnotationPresent(SimpleCommand.class)).forEach(m -> {

            SimpleCommand cmdInfo = m.getAnnotation(SimpleCommand.class);

            Parameter[] parameters = m.getParameters();
            int paramCount = parameters.length;
            int senderIdx = -1;
            int optionalStartIds = -1;

            for (int i = 0; i < paramCount; i++) {
                Parameter param = parameters[i];
                if (param.isAnnotationPresent(SimpleCommand.Sender.class)) {
                    if (senderIdx != -1) {
                        // 多个Sender参数无意义，禁止
                        this.messageSender.warningConsole(msgPath + "fail.tooManySenders",
                                                          this.handleCommand,
                                                          cmdInfo.name());
                        return;
                    } else if (!CommandSender.class.isAssignableFrom(param.getType())) {
                        // Sender参数类型必须是CommandSender/子接口/实现类
                        this.messageSender.warningConsole(msgPath + "fail.unsupportedSender",
                                                          this.handleCommand,
                                                          cmdInfo.name());
                        return;
                    }
                    senderIdx = i;
                }
                if (param.isAnnotationPresent(SimpleCommand.OptionalStart.class)) {
                    if (optionalStartIds == -1) optionalStartIds = i;
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
            // 获取参数转换器
            MethodHandle[] converters = new MethodHandle[needSender ? paramCount - 1 : paramCount];
            for (int i = 0; i < paramCount; i++) {
                if (needSender && i == senderIdx) continue;
                ParameterConverter<?> converter = ParameterConverter.getConverter(parameters[i].getType());
                if (converter == null) {
                    // Sender参数类型必须是CommandSender/子接口/实现类
                    this.messageSender.warningConsole(msgPath + "fail.cantFoundParamConverter",
                                                      this.handleCommand,
                                                      cmdInfo.name(),
                                                      parameters[i].getType().getName());
                    return;
                }

                if (needSender && i > senderIdx) converters[i - 1] = converter.toMethodHandle();
                else converters[i] = converter.toMethodHandle();
            }

            // 绑定到原始对象
            // 必须最先绑定，后续处理都忽略了此方法句柄的this参数
            handle = handle.bindTo(command);

            if (senderIdx > 0) {// Sender参数位置不在第一个的将其移到第一个
                Class<?>[] args = m.getParameterTypes();
                Class<?> senderType = args[senderIdx];
                args[senderIdx] = args[0];
                args[0] = senderType;

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
                for (int i = 0; i < idxs.length; i++) {
                    if (i > senderIdx) idxs[i] = i;
                    else idxs[i] = i + 1;
                }
                idxs[senderIdx] = 0;

                handle = MethodHandles.permuteArguments(handle, MethodType.methodType(m.getReturnType(), args), idxs);
            } else if (!needSender) {// 没有Sender参数的在最前面加上Sender参数
                handle = MethodHandles.dropArguments(handle, 0, CommandSender.class);
            }
            // 过滤参数
            handle = MethodHandles.filterArguments(handle, 1, converters);
            // 字符串数组转为参数列表
            handle = handle.asSpreader(String[].class, converters.length);

            // 参数最大数量，忽略Sender参数
            int maxArgCount = paramCount;
            if (needSender) maxArgCount -= 1;

            // 参数最小数量，有可选参数时是optionalStartIds，无可选参数时是最大参数数量
            int minArgCount = optionalStartIds > 0 ? optionalStartIds : paramCount;
            // 同时有必填参数和可选参数且Sender参数的位置在optionalStartIds前面时，忽略Sender参数
            if (needSender && (optionalStartIds <= 0 || senderIdx < optionalStartIds)) minArgCount -= 1;


            this.registerCommand(new SimpleCommandImpl(cmdInfo, this.messageSender, handle,
                                                       needSender ? parameters[senderIdx].getType().asSubclass(
                                                               CommandSender.class) : null,
                                                       minArgCount, maxArgCount));
        });
    }

    @Override
    public void unregisterCommand(String cmdName) {
        this.commands.remove(cmdName);
    }

    static class SimpleCommandImpl extends LMCCommand {

        final MethodHandle handle;
        private final Class<? extends CommandSender> senderType;
        private final int minArgCount;
        private final int maxArgCount;

        private MessageSender messageSender;

        private SimpleCommandImpl(SimpleCommand cmdInfo, MessageSender messageSender, MethodHandle handle, Class<? extends CommandSender> senderType, int minArgCount, int maxArgCount) {
            super(cmdInfo.name(), cmdInfo.description(), cmdInfo.aliases(), cmdInfo.permissions());

            this.messageSender = messageSender;

            this.handle = handle;
            this.senderType = senderType;
            this.minArgCount = minArgCount;
            this.maxArgCount = maxArgCount;
        }

        @Override
        public void execute(CommandSender sender, String... args) {// TODO 提示
            if (this.senderType != null && !this.senderType.isInstance(sender)) {
                //this.onSenderTypeDisallow(sender, args);
                return;
            } else if (args.length > this.maxArgCount) {
                //this.onTooManyArgs(sender, args);
                System.out.println("max");
                return;
            } else if (args.length < this.minArgCount) {
                //this.onTooLittleArgs(sender, args);
                System.out.println("min");
                return;
            }
            if (args.length < this.maxArgCount) {
                args = Arrays.copyOf(args, this.maxArgCount);
            }
            try {
                this.handle.invoke(sender, args);
            } catch (ArgConverterFailException e) {
                //this.onArgConvertFail(sender, e.getArg(), e.getConvertTo(), args);
                System.out.println("convert");
            } catch (WrongMethodTypeException | ClassCastException e) {
                // 该项错误不应该出现，若出现则是DefaultCommandManager类的registerCommands(SimpleLMCCommand command)方法有bug
                e.printStackTrace();
                ExceptionHandler.handle(e);
                this.messageSender.warningConsole("bug.simpleCommandRegister", this.getName());
            } catch (Throwable e) {// 该项为命令处理方法抛出的异常
                e.printStackTrace();
                ExceptionHandler.handle(e);
            }
        }

        @Override
        public void showHelp(CommandSender sender) {

        }
    }
}
