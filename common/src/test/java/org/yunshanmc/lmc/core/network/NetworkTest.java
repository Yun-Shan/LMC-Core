package org.yunshanmc.lmc.core.network;

import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;
import org.yunshanmc.lmc.core.MockPlugin;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class NetworkTest {

    @Test
    public void start() throws Exception {
        PacketType.register(10001, TestPacket.class);
        String testVal = "Test Value~Yeah!";
        int port = 28080;

        CountDownLatch countDown = new CountDownLatch(1);

        NetworkServer server = new NetworkServer(port);
        server.start(MockPlugin.newInstance().getMessageManager().getMessageSender(), () -> new AbstractPacketHandler[]{
            new AbstractPacketHandler() {
                protected void handle(ChannelHandlerContext ctx, TestPacket msg) throws Exception {
                    long re = System.currentTimeMillis();
                    System.out.println("re   time: " + re);
                    assertEquals(msg.val, testVal);
                    ctx.channel().writeAndFlush(new TestPacket(testVal));
                    countDown.countDown();
                }
            }});
        NetworkClient client = new NetworkClient("TestClient", port, MockPlugin.newInstance().getMessageManager().getMessageSender());
        client.start(() ->
            new AbstractPacketHandler[]{
                new AbstractPacketHandler() {
                    protected void handle(ChannelHandlerContext ctx, TestPacket msg) throws Exception {
                        long re = System.currentTimeMillis();
                        System.out.println("cl re time: " + re);
                        assertEquals(msg.val, testVal);
                    }
                }
            });
        client.sendPacket(new TestPacket(testVal));
        client.stop();
        server.getChannelByName("TestClient").channel().writeAndFlush(new TestPacket(testVal));
        Thread.sleep(5000);
        client = new NetworkClient("TestClient", port, MockPlugin.newInstance().getMessageManager().getMessageSender());
        client.start(() ->
            new AbstractPacketHandler[]{
                new AbstractPacketHandler() {
                    protected void handle(ChannelHandlerContext ctx, TestPacket msg) throws Exception {
                        long re = System.currentTimeMillis();
                        System.out.println("cl re time: " + re);
                        assertEquals(msg.val, testVal);
                    }
                }
            });
        long init, send;
        init = System.currentTimeMillis();
        client.sendPacket(new TestPacket(testVal));
        send = System.currentTimeMillis();
        System.out.println("init time: " + init);
        System.out.println("send time: " + send);
        countDown.await();
        Thread.sleep(500);
        client.stop();
        server.stop();
    }

    public static class TestPacket extends AbstractPacket {

        private String val;

        public TestPacket(String val) {
            this.val = val;
        }

        public TestPacket() {

        }

        @Override
        public void read(DataBuffer buffer) {
            this.val = buffer.readString();
        }

        @Override
        public void write(DataBuffer buffer) {
            buffer.writeString(this.val);
        }
    }
}