package org.yunshanmc.lmc.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
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

    public DefaultCommandManager(LMCPlugin plugin, String handleCommand) {
        this.handleCommand = handleCommand;
        PluginCommand command = plugin.getCommand(handleCommand);
        command.setExecutor(this);
        command.setTabCompleter(this);

        this.messageSender = plugin.getMessageManager().getMessageSender();
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

            if (senderIdx > 0) {// Sender参数位置不在第一个的将其移到第一个
                Class<?>[] args = m.getParameterTypes();
                Class<?> senderType = args[senderIdx];
                args[senderIdx] = args[0];
                args[0] = senderType;

                int[] idxs = new int[args.length];
                for (int i = 0; i < idxs.length; i++) {
                    idxs[i] = i;
                }
                idxs[0] = senderIdx;
                idxs[senderIdx] = 0;

                handle = MethodHandles.permuteArguments(handle, MethodType.methodType(m.getReturnType(), args), idxs);
            } else {// 没有Sender参数的在最前面加上Sender参数
                handle = MethodHandles.dropArguments(handle, 0, CommandSender.class);
            }
            // 过滤参数
            handle = MethodHandles.filterArguments(handle, 1, converters);
            // 绑定到原始对象
            handle = handle.bindTo(command);

            this.registerCommand(new SimpleCommandImpl(cmdInfo, this.messageSender, handle,
                                                       needSender ? parameters[senderIdx].getType().asSubclass(
                                                      CommandSender.class) : null,
                                              optionalStartIds > 0 ? optionalStartIds : paramCount, paramCount));
        });
    }

    @Override
    public void unregisterCommand(String cmdName) {
        this.commands.remove(cmdName);
    }

    private static class SimpleCommandImpl extends LMCCommand {

        private final MethodHandle handle;
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
        public void execute(CommandSender sender, String... args) {
            if (this.senderType != null && !this.senderType.isInstance(sender)) {
                //this.onSenderTypeDisallow(sender, args);
                return;
            } else if (args.length > this.maxArgCount) {
                //this.onTooManyArgs(sender, args);
                return;
            } else if (args.length < this.minArgCount) {
                //this.onTooLittleArgs(sender, args);
                return;
            }
            if (args.length < this.maxArgCount) {
                args = Arrays.copyOf(args, this.maxArgCount);
            }
            try {
                this.handle.invoke(sender, args);
            } catch (ArgConverterFailException e) {
                //this.onArgConvertFail(sender, e.getArg(), e.getConvertTo(), args);
            } catch (WrongMethodTypeException | ClassCastException e) {
                // 该项错误不应该出现，若出现则是DefaultCommandManager类的registerCommands(SimpleLMCCommand command)方法有bug
                this.messageSender.warningConsole("bug.simpleCommandRegister", this.getName());
            } catch (Throwable e) {// 该项为命令处理方法抛出的异常
                ExceptionHandler.handle(e);
            }
        }

        @Override
        public void showHelp(CommandSender sender) {

        }
    }
}
