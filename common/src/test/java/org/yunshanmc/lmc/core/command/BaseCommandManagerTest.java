package org.yunshanmc.lmc.core.command;

import org.junit.Test;
import org.yunshanmc.lmc.core.MockPlugin;
import org.yunshanmc.lmc.core.command.executors.BaseCommandExecutor;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class BaseCommandManagerTest {

    @Test
    @SuppressWarnings("all")
    public void registerCommands() throws Exception {
        // TODO alias, label测试

        MockPlugin plugin = MockPlugin.newInstance();

        MockCommandManager manager = new MockCommandManager(plugin, "test");

        AtomicInteger counter = new AtomicInteger(0);

        MockPlayer mockPlayer = new MockPlayer("[$Test$]");
        SimpleLMCCommand simpleCommand = new SimpleLMCCommand() {

            @SimpleCommand(name = "test0", aliases = "")
            public void test0(int val) {
                assertEquals(val, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test1", useDefaultPermission = true)
            public void test1() {
                assertEquals(2, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test2", permissions = "perm.test2")
            public void test2(String str1, int int1) {
                assertEquals(233, int1);
                assertEquals("qwq", str1);
                assertEquals(3, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test3", permissions = {"perm.test3a", "perm.test3b"})
            public void test3(@SimpleCommand.Sender MockPlayer player) {
                assertEquals(mockPlayer, player);
                assertEquals(4, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test4")
            public void test4(@SimpleCommand.Sender MockPlayer player, String str1, @SimpleCommand.OptionalStart String str2) {
                assertEquals("owo", str1);
                assertEquals(5, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test5")
            public void test5(@SimpleCommand.Sender MockPlayer player, String str1, @SimpleCommand.OptionalStart String str2) {
                assertEquals("owo", str1);
                assertEquals("hhh", str2);
                assertEquals(6, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test6")
            public void test6(int int1, @SimpleCommand.OptionalStart String str2, @SimpleCommand.Sender MockPlayer player) {
                assertEquals(666, int1);
                assertEquals("hhh", str2);
                assertEquals(7, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test7", aliases = "test7-1")
            public void test7(@SimpleCommand.RawInfo SimpleCommand.CommandRawInfo rawInfo, @SimpleCommand.Sender MockPlayer player) {
                assertEquals("t", rawInfo.getLabel());
                assertEquals(8, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test8", aliases = "test8-1")
            public void test8(@SimpleCommand.RawInfo SimpleCommand.CommandRawInfo rawInfo) {
                assertEquals("t1", rawInfo.getLabel());
                assertEquals(9, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test9", aliases = "test9-1")
            public void test9(int int1, @SimpleCommand.RawInfo SimpleCommand.CommandRawInfo rawInfo, @SimpleCommand.OptionalStart String str2, @SimpleCommand.Sender MockPlayer player) {
                assertEquals("t2", rawInfo.getLabel());
                assertEquals(777, int1);
                assertEquals("l77", str2);
                assertEquals(10, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test10", aliases = {"test10-1", "test10-2"})
            public void test10(@SimpleCommand.Sender MockPlayer player, @SimpleCommand.RawInfo SimpleCommand.CommandRawInfo rawInfo, @SimpleCommand.OptionalStart int int1) {
                assertEquals("ttt", rawInfo.getLabel());
                assertEquals(777, int1);
                assertEquals(11, counter.get());
                counter.incrementAndGet();
            }

            @SimpleCommand(name = "test11")
            public void test11(@SimpleCommand.Sender LMCCommandSender sender, String str1, @SimpleCommand.OptionalStart int int1) {
                assertEquals("[$Test$]", ((MockPlayer) sender.getHandle()).getName());
                assertEquals("qwq", str1);
                assertEquals(777, int1);
                assertEquals(12, counter.get());
                counter.incrementAndGet();
            }
        };
        manager.registerCommands(simpleCommand);

        BaseCommandExecutor executor = manager.getCommandExecutor();
        executor.executeCommand(mockPlayer, "test", "", "0");
        executor.executeCommand(mockPlayer, "test", "test0", "1");
        mockPlayer.addPermissions("test.test1");
        executor.executeCommand(mockPlayer, "test", "test1");
        mockPlayer.addPermissions("perm.test2");
        executor.executeCommand(mockPlayer, "test", "test2", "qwq", "233");
        mockPlayer.addPermissions("perm.test3a", "perm.test3b");
        executor.executeCommand(mockPlayer, "test", "test3");
        executor.executeCommand(mockPlayer, "test", "test4", "owo");
        executor.executeCommand(mockPlayer, "test", "test5", "owo", "hhh");
        executor.executeCommand(mockPlayer, "test", "test6", "666", "hhh");
        executor.executeCommand(mockPlayer, "t", "test7-1");
        executor.executeCommand(mockPlayer, "t1", "test8-1");
        executor.executeCommand(mockPlayer, "t2", "test9-1", "777", "l77");
        executor.executeCommand(mockPlayer, "ttt", "test10-2", "777");
        executor.executeCommand(mockPlayer, "test", "test11", "qwq", "777");
        assertEquals(13, counter.get());
    }

}