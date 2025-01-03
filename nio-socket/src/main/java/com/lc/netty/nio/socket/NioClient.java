package com.lc.netty.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author by licheng01
 * @date 2025/1/3 10:24
 * @description
 */
public class NioClient {

    public static void main(String[] args) throws IOException {
        // 创建 Selector
        Selector selector = Selector.open();
        // 创建 SocketChannel 并连接到服务器
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false); // 设置为非阻塞模式
        socketChannel.connect(new InetSocketAddress("localhost", 8080));
        // 将 SocketChannel 注册到 Selector，监听 CONNECT 事件
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            // 阻塞等待就绪的 Channel
            System.out.println("--client 等待就绪--");
            selector.select();
            System.out.println("--client 开始处理--");
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isConnectable()) {
                    // 处理连接完成事件
                    SocketChannel channel = (SocketChannel) key.channel();
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }
                    channel.register(selector, SelectionKey.OP_READ);
                    System.out.println("已连接到服务器，请输入消息:");
                } else if (key.isReadable()) {
                    // 处理服务器数据
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int bytesRead = channel.read(buffer);

                    if (bytesRead == -1) {
                        // 服务器关闭连接
                        System.out.println("服务器已断开");
                        channel.close();
                        return;
                    } else if (bytesRead > 0) {
                        // 处理接收到的数据
                        buffer.flip();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        String message = new String(data);
                        System.out.println("收到服务器回复: " + message);
                    }
                }
            }

            // 从控制台读取输入并发送到服务器
            if (socketChannel.isConnected() && System.in.available() > 0) {
                String input = scanner.nextLine();
                ByteBuffer buffer = ByteBuffer.wrap(input.getBytes());
                socketChannel.write(buffer);
            }
        }
    }

}
