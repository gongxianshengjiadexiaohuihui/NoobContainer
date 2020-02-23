package com.ggp.connector.http;

import com.ggp.common.Constants;
import com.ggp.connector.processor.HttpProcessor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author ggp
 * @Date 2020/2/1 18:41
 * @Description
 */
public class HttpConnector implements Runnable {
    private boolean stopped;
    private String scheme="http";

    public String getScheme() {
        return scheme;
    }

    /**
     * 1、等待http请求
     * 2、为每个请求创建HttpProcessor实例
     * 3、调用HttpProcessor的parse方法
     */
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Constants.DEFAULT_PORT, 1, InetAddress.getByName(Constants.DEFAULT_IP));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!stopped){
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                continue;
            }
            HttpProcessor httpProcessor = new HttpProcessor(this);
            httpProcessor.process(socket);
        }
    }

    /**
     * 创建线程
     */
    public void start(){
        Thread thread = new Thread(this);
        thread.start();

    }
}
