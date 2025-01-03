package com.lc.netty.bio.socket.netty.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author by licheng01
 * @date 2025/1/3 10:01
 * @description
 */
public class AppendNewlineHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        System.out.println("--消息添加--");
        if (msg instanceof String) {
            // 在消息末尾添加 \n
            msg = msg + "\n";
        }
        ctx.write(msg, promise).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                System.out.println("a-消息发送成功！");
            } else {
                System.out.println("a-消息发送失败: " + future.cause().getMessage());
            }
        });
    }

}
