package org.yunshanmc.lmc.core.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.yunshanmc.lmc.core.command.executors.CommandExecutor;
import org.yunshanmc.lmc.core.message.DefaultMessageFormat;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class DefaultCommandManagerTest {
    @Test
    @SuppressWarnings("all")
    public void registerCommands() throws Exception {
        CommandSender sender = (CommandSender) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                                                                      new Class[]{CommandSender.class},
                                                                      (proxy, method, args) -> method.invoke(proxy, args));
        Player fakePlayer = (Player) Proxy.newProxyInstance(DefaultMessageFormat.class.getClassLoader(),
                                                            new Class<?>[]{Player.class},
                                                            (proxy, method, args) -> method.invoke(proxy, args));
        Field fexecutor = DefaultCommandManager.class.getDeclaredField("commandExecutor");
        fexecutor.setAccessible(true);

        DefaultCommandManager manager = new DefaultCommandManager();
        AtomicReference<Map<String, LMCCommand>> ar = new AtomicReference<>();
        CommandExecutor executor = new CommandExecutor(manager, null) {
            {
                ar.set(this.commands);
            }
        };
        fexecutor.set(manager, executor);
        Map<String, LMCCommand> commands = ar.get();

        AtomicInteger counter = new AtomicInteger(0);

        SimpleLMCCommand simpleCommand = new SimpleLMCCommand() {

            @SimpleCommand
            public void test0() {
                assertEquals(0, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test")
            public void test1() {
                assertEquals(1, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test2")
            public void test2(String str1, int int1) {
                assertEquals(233, int1);
                assertEquals("qwq", str1);
                assertEquals(2, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test3")
            public void test3(@SimpleCommand.Sender Player player) {
                assertEquals(3, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test4")
            public void test4(@SimpleCommand.Sender Player player, String str1, @SimpleCommand.OptionalStart String str2) {
                assertEquals("owo", str1);
                assertEquals(4, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test5")
            public void test5(@SimpleCommand.Sender Player player, String str1, @SimpleCommand.OptionalStart String str2) {
                assertEquals("owo", str1);
                assertEquals("hhh", str2);
                assertEquals(5, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test6")
            public void test6(int int1, @SimpleCommand.OptionalStart String str2, @SimpleCommand.Sender Player player) {
                assertEquals(666, int1);
                assertEquals("hhh", str2);
                assertEquals(6, counter.get());
                counter.incrementAndGet();
            }
        };
        manager.registerCommands(simpleCommand);

        commands.get("").execute(sender);
        commands.get("test").execute(sender);
        commands.get("test2").execute(sender, "qwq", "233");
        commands.get("test3").execute(fakePlayer);
        commands.get("test4").execute(fakePlayer, "owo");
        commands.get("test5").execute(fakePlayer, "owo", "hhh");
        commands.get("test6").execute(fakePlayer, "666", "hhh");
        assertEquals(7, counter.get());
    }

}