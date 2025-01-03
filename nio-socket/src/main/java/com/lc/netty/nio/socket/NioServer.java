package com.lc.netty.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author by licheng01
 * @date 2025/1/3 10:23
 * @description
 */
public class NioServer {

    public static void main(String[] args) throws IOException {
        // 创建 Selector
        Selector selector = Selector.open();

        // 创建 ServerSocketChannel 并绑定端口
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 8080));
        serverSocketChannel.configureBlocking(false); // 设置为非阻塞模式

        // 将 ServerSocketChannel 注册到 Selector，监听 ACCEPT 事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器已启动，等待客户端连接...");

        while (true) {
            // 阻塞等待就绪的 Channel
            System.out.println("--server 等待select()");
            selector.select();
            System.out.println("--server 开始处理");
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            operateSelect(selector, iterator);
        }
    }

    private static void operateSelect(Selector selector, Iterator<SelectionKey> iterator) {
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();
            operate(selector, key);
        }
    }


    private static void operate(Selector selector, SelectionKey key) {

        try {
            if (key.isAcceptable()) {
                // 处理客户端连接
                ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                SocketChannel clientChannel = serverChannel.accept();
                clientChannel.configureBlocking(false);
                clientChannel.register(selector, SelectionKey.OP_READ);
                System.out.println("客户端已连接: " + clientChannel.getRemoteAddress());
            } else if (key.isReadable()) {
                System.out.println("key is readable()");
                // 处理客户端数据
                SocketChannel clientChannel = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bytesRead = clientChannel.read(buffer);
                if (bytesRead == -1) {
                    // 客户端关闭连接
                    System.out.println("客户端已断开: " + clientChannel.getRemoteAddress());
                    clientChannel.close();
                } else if (bytesRead > 0) {
                    // 处理接收到的数据
                    buffer.flip();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    String message = new String(data);
                    System.out.println("收到客户端消息: " + message);

                    // 回复客户端
                    String response = "服务器回复: " + message;
                    ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
                    clientChannel.write(responseBuffer);
                }
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
            if (key != null) {
                key.cancel(); // 从 Selector 中移除
                try {
                    key.channel().close(); // 关闭发生异常的 Channel
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }
}
