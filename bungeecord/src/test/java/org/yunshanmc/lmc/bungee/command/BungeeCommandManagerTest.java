package org.yunshanmc.lmc.bungee.command;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.junit.Test;
import org.yunshanmc.lmc.bungee.EnvironmentUtil;
import org.yunshanmc.lmc.bungee.MockPlugin;
import org.yunshanmc.lmc.core.bungee.command.BungeeLMCCommandSender;
import org.yunshanmc.lmc.core.command.*;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class BungeeCommandManagerTest {
    @Test
    @SuppressWarnings("all")
    public void registerCommands() throws Exception {
        // TODO alias, label测试

        EnvironmentUtil.mockBukkit();

        MockPlugin plugin = MockPlugin.newInstance();

        AtomicReference<Map<String, AbstractLMCCommand>> ar = new AtomicReference<>();
        BaseCommandManager manager = new MockCommandManager(plugin, "test", ar);
        Map<String, AbstractLMCCommand> commands = ar.get();

        AtomicInteger counter = new AtomicInteger(0);

        SimpleLMCCommand simpleCommand = new SimpleLMCCommand() {

            @SimpleCommand(name = "test0", aliases = "")
            public void test0(int val) {
                assertEquals(val, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test1")
            public void test1() {
                assertEquals(2, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test2")
            public void test2(String str1, int int1) {
                assertEquals(233, int1);
                assertEquals("qwq", str1);
                assertEquals(3, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test3")
            public void test3(@SimpleCommand.Sender ProxiedPlayer player) {
                assertEquals(4, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test4")
            public void test4(@SimpleCommand.Sender ProxiedPlayer player, String str1, @SimpleCommand.OptionalStart
                String str2) {
                assertEquals("owo", str1);
                assertEquals(5, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test5")
            public void test5(@SimpleCommand.Sender ProxiedPlayer player, String str1, @SimpleCommand.OptionalStart
                String str2) {
                assertEquals("owo", str1);
                assertEquals("hhh", str2);
                assertEquals(6, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test6")
            public void test6(int int1, @SimpleCommand.OptionalStart String str2, @SimpleCommand.Sender ProxiedPlayer
                player) {
                assertEquals(666, int1);
                assertEquals("hhh", str2);
                assertEquals(7, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test7")
            public void test7(@SimpleCommand.RawInfo SimpleCommand.CommandRawInfo rawInfo, @SimpleCommand.Sender
                ProxiedPlayer
                              player) {
                assertEquals("t", rawInfo.getLabel());
                assertEquals(8, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test8")
            public void test8(@SimpleCommand.RawInfo SimpleCommand.CommandRawInfo rawInfo) {
                assertEquals("t1", rawInfo.getLabel());
                assertEquals(9, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test9")
            public void test9(int int1, @SimpleCommand.RawInfo SimpleCommand.CommandRawInfo rawInfo, @SimpleCommand
                .OptionalStart String str2, @SimpleCommand.Sender ProxiedPlayer player) {
                assertEquals("t2", rawInfo.getLabel());
                assertEquals(777, int1);
                assertEquals("l77", str2);
                assertEquals(10, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test10")
            public void test10(@SimpleCommand.Sender ProxiedPlayer player, @SimpleCommand.RawInfo SimpleCommand
                .CommandRawInfo
                rawInfo, @SimpleCommand.OptionalStart int int1) {
                assertEquals("ttt", rawInfo.getLabel());
                assertEquals(777, int1);
                assertEquals(11, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test11")
            public void test11(@SimpleCommand.Sender BaseLMCCommandSender sender, String str1, @SimpleCommand.OptionalStart int int1) {
                assertEquals("[$Test$]", ((ProxiedPlayer)sender.getHandle()).getName());
                assertEquals("qwq", str1);
                assertEquals(777, int1);
                assertEquals(12, counter.get());
                counter.incrementAndGet();
            }
        };
        manager.registerCommands(simpleCommand);

        ProxiedPlayer fakeBukkitPlayer = (ProxiedPlayer) Proxy.newProxyInstance(
            BungeeCommandManagerTest.class.getClassLoader(),
            new Class<?>[]{ProxiedPlayer.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "sendMessage":
                        if (args[0] instanceof String) System.out.println(args[0]);
                        else if (args[0] instanceof String[]) for (String str : (String[])args[0]) System.out.println(str);
                        return null;
                    case "getName":
                    case "getDisplayName":
                    case "getCustomName":
                    case "getPlayerListName":
                        return "[$Test$]";
                    default:
                        throw new UnsupportedOperationException();
                }
            });
        BaseLMCCommandSender fakePlayer = new BungeeLMCCommandSender(fakeBukkitPlayer, plugin.getMessageManager()
            .getMessageSender());
        commands.get("").execute(fakePlayer, "test", "0");
        commands.get("test0").execute(fakePlayer, "test", "1");
        commands.get("test1").execute(fakePlayer, "test");
        commands.get("test2").execute(fakePlayer, "test", "qwq", "233");
        commands.get("test3").execute(fakePlayer, "test");
        commands.get("test4").execute(fakePlayer, "test", "owo");
        commands.get("test5").execute(fakePlayer, "test", "owo", "hhh");
        commands.get("test6").execute(fakePlayer, "test", "666", "hhh");
        commands.get("test7").execute(fakePlayer, "t");
        commands.get("test8").execute(fakePlayer, "t1");
        commands.get("test9").execute(fakePlayer, "t2", "777", "l77");
        commands.get("test10").execute(fakePlayer, "ttt", "777");
        commands.get("test11").execute(fakePlayer, "test", "qwq", "777");
        assertEquals(13, counter.get());
    }

}