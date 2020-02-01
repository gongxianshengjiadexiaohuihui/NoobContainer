package com.ggp.connector.http;

import com.ggp.common.Constants;

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
            HttpProcessor httpProcessor = new HttpProcessor();
            httpProcessor.parse(socket);
        }
    }
    public void start(){
        Thread thread = new Thread(this);
        thread.start();

    }
}
