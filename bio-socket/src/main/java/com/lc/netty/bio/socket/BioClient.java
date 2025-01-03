package com.lc.netty.bio.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author by licheng01
 * @date 2025/1/3 9:08
 * @description
 */
public class BioClient {

    public static void main(String[] args) {
        String host = "localhost"; // 服务器地址
        int port = 8080; // 服务器端口

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("已连接到服务器，请输入消息:");
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                // 发送消息到服务器
                out.println(userInput);

                // 接收服务器的回复
                String serverResponse = in.readLine();
                System.out.println("服务器回复: " + serverResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
