package com.lc.netty.bio.socket.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author by licheng01
 * @date 2025/1/3 9:41
 * @description
 */
public class NettyClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new StringEncoder(),
                                    new AppendNewlineHandler(),
                                    new EchoClientHandler()
                            );
                        }
                    });

            ChannelFuture f = b.connect("localhost", 8080).addListener((ChannelFuture future) -> {
                if (future.isSuccess()) {
                    System.out.println("连接成功！");
                } else {
                    System.out.println("连接失败: " + future.cause().getMessage());
                }
            }).sync();

            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }


}
