package org.yunshanmc.lmc.core.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yun-Shan
 */
@SuppressWarnings("unchecked")
public abstract class AbstractPacketHandler extends SimpleChannelInboundHandler<AbstractPacket> {

    private final Map<Class<? extends AbstractPacket>, MethodHandle> handles = new HashMap<>();

    {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (// 单参数并且参数为包
                ((method.getParameterCount() == 1 && AbstractPacket.class.isAssignableFrom(method.getParameterTypes()[0]))
                    // 双参数并且第一个参数是context，第二个参数是包
                    || (method.getParameterCount() == 2 && ChannelHandlerContext.class.equals(method.getParameterTypes()[0]) && AbstractPacket.class.isAssignableFrom(method.getParameterTypes()[1])))) {
                try {
                    method.setAccessible(true);
                    MethodHandle handle = lookup.unreflect(method).bindTo(this);
                    if (method.getParameterCount() == 1) {
                        handle = MethodHandles.dropArguments(handle, 0, ChannelHandlerContext.class);
                    }
                    //noinspection unchecked
                    this.handles.put((Class<? extends AbstractPacket>) method.getParameterTypes()[method.getParameterCount() - 1], handle);
                } catch (ReflectiveOperationException e) {
                    ExceptionHandler.handle(e);
                }
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractPacket packet) {
        Class<? extends AbstractPacket> cls = packet.getClass();
        do {
            MethodHandle handle = this.handles.get(cls);
            if (handle != null) {
                try {
                    handle.invoke(ctx, packet);
                } catch (Throwable t) {
                    ExceptionHandler.handle(t);
                }
            }
            Class newCls = cls.getSuperclass();
            if (AbstractPacket.class.isAssignableFrom(newCls)) {
                cls = newCls;
            } else {
                break;
            }
        } while (true);
        ctx.fireChannelRead(packet);
    }
}
