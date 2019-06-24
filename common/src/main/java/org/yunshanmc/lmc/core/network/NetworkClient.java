package org.yunshanmc.lmc.core.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.network.packet.RegisterClientPacket;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author Yun-Shan
 */
public class NetworkClient {
    private final String name;
    private final int port;
    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private ConcurrentMap<Integer, BiConsumer<ChannelHandlerContext, ? extends BaseResponsivePacket>> responseHandles = new ConcurrentHashMap<>();

    private volatile boolean started;
    private volatile boolean stoped;

    private final MessageSender messageSender;

    private final PacketType packetType = new PacketType();

    public NetworkClient(String name, int port, MessageSender messageSender) {
        this.name = name;
        this.port = port;
        this.messageSender = messageSender;
    }

    public boolean start(Supplier<ChannelHandler[]> getHandlers) {
        if (this.started) {
            return true;
        }
        this.eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("localhost", this.port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new PacketDecoder(packetType));
                        ch.pipeline().addLast(new PacketEncoder(packetType));
                        if (getHandlers != null) {
                            ch.pipeline().addLast(getHandlers.get());
                        }
                        ch.pipeline().addLast(new AbstractPacketHandler() {
                            public void handle(ChannelHandlerContext ctx, BaseResponsivePacket packet) {
                                //noinspection unchecked
                                BiConsumer<ChannelHandlerContext, BaseResponsivePacket> handler = (BiConsumer<ChannelHandlerContext, BaseResponsivePacket>) responseHandles.remove(packet.getResponseId());
                                if (handler != null) {
                                    handler.accept(ctx, packet);
                                }
                            }
                        });
                        // 断线重连
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                ExceptionHandler.handle(new RuntimeException(cause));
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                if (stoped) {
                                    messageSender.infoConsole("network.client.Disconnected");
                                } else {
                                    messageSender.warningConsole("network.client.AccidentallyDisconnected");
                                }
                            }

                            @Override
                            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                                if (!stoped) {
                                    long delay = 10L;
                                    messageSender.warningConsole("network.client.TryingReconnect", delay);
                                    eventLoopGroup.schedule(() -> doConnect(bootstrap), delay, TimeUnit.SECONDS);
                                }
                            }
                        });
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
            this.doConnect(bootstrap);
            this.started = true;
            return true;
        } catch (RuntimeException ex) {
            this.eventLoopGroup.shutdownGracefully();
            ExceptionHandler.handle(ex);
            return false;
        }
    }

    private void doConnect(Bootstrap bootstrap) {
        ChannelFuture future = bootstrap.connect().syncUninterruptibly();
        if (future.isSuccess()) {
            this.messageSender.infoConsole("network.client.Connected");
            this.channel = future.channel();
            this.channel.writeAndFlush(new RegisterClientPacket(this.name));
            this.onConnected();
        }
    }

    protected void onConnected() {
    }

    public void stop() {
        if (this.stoped) {
            return;
        }
        this.stoped = true;
        if (this.eventLoopGroup != null) {
            this.eventLoopGroup.shutdownGracefully().syncUninterruptibly();
            this.eventLoopGroup = null;
        }
    }

    public void sendPacket(AbstractPacket packet) {
        this.channel.writeAndFlush(packet);
    }

    public <T extends BaseResponsivePacket> void sendResponsivePacket(BaseResponsivePacket packet, BiConsumer<ChannelHandlerContext, T> responseHandle) {
        this.responseHandles.put(packet.getResponseId(), responseHandle);
        this.sendPacket(packet);
    }

    public PacketType getPacketType() {
        return this.packetType;
    }
}
