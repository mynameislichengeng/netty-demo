package com.lc.netty.bio.socket.netty.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author by licheng01
 * @date 2025/1/3 10:11
 * @description
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive 被调用，准备发送消息...");
        ctx.writeAndFlush("Hello, Server!").addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                System.out.println("消息发送成功！");
            } else {
                System.out.println("消息发送失败: " + future.cause().getMessage());
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 接收服务器的回复
        System.out.println("客户端收到: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
