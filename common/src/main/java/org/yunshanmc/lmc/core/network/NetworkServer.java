package org.yunshanmc.lmc.core.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.network.packet.RegisterClientPacket;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * @author Yun-Shan
 */
public class NetworkServer {
    private final int port;
    private EventLoopGroup eventLoopGroup;
    private ClientHandler clientHandler;

    private final PacketType packetType = new PacketType();

    private static final AttributeKey<String> CLIENT_NEME = AttributeKey.valueOf("lmc.network.server.clientName");

    public NetworkServer(int port) {
        this.port = port;
    }

    public boolean start(MessageSender messageSender, Supplier<ChannelHandler[]> getHandlers) {
        this.eventLoopGroup = new NioEventLoopGroup();
        this.clientHandler = new ClientHandler(messageSender);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new PacketDecoder(packetType));
                        ch.pipeline().addLast(new PacketEncoder(packetType));
                        ch.pipeline().addLast(clientHandler);
                        if (getHandlers != null) {
                            ch.pipeline().addLast(getHandlers.get());
                        }
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                ExceptionHandler.handle(new RuntimeException(cause));
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                messageSender.infoConsole("network.server.ClientDisconnected", ctx.channel().attr(CLIENT_NEME).get());
                            }

                            @Override
                            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                                clientHandler.removeChannel(ctx);
                                messageSender.infoConsole("network.server.ClientRemoved", ctx.channel().attr(CLIENT_NEME).get());
                            }
                        });
                    }
                })
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
            b.bind(this.port).syncUninterruptibly();
            return true;
        } catch (RuntimeException ex) {
            this.eventLoopGroup.shutdownGracefully();
            ExceptionHandler.handle(ex);
            return false;
        }
    }

    public void stop() {
        if (this.eventLoopGroup != null) {
            this.eventLoopGroup.shutdownGracefully().syncUninterruptibly();
            this.eventLoopGroup = null;
        }
    }

    public void broadcast(AbstractPacket packet) {
        this.clientHandler.name2channel.values().forEach(ctx -> ctx.channel().writeAndFlush(packet));
    }

    public ChannelHandlerContext getChannelByName(String name) {
        return this.clientHandler.getChannelByName(name);
    }

    public PacketType getPacketType() {
        return this.packetType;
    }

    @ChannelHandler.Sharable
    private static class ClientHandler extends AbstractPacketHandler {

        private final MessageSender sender;
        private final HashMap<String, ChannelHandlerContext> name2channel = new HashMap<>();

        public ClientHandler(MessageSender messageSender) {
            this.sender = messageSender;
        }

        public void handle(ChannelHandlerContext ctx, RegisterClientPacket packet) {
            this.name2channel.put(packet.getName(), ctx);
            ctx.channel().attr(CLIENT_NEME).set(packet.getName());
            this.sender.debugConsole(3, "network.packet.SubServerInfo.Add", packet.getName());
        }

        public ChannelHandlerContext getChannelByName(String name) {
            return this.name2channel.get(name);
        }

        void removeChannel(ChannelHandlerContext ctx) {
            this.name2channel.remove(ctx.channel().attr(CLIENT_NEME).get());
        }
    }
}
