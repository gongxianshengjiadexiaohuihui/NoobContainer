package com.ggp.server;

import com.ggp.common.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: ggp
 * @Date: 2020/1/20 15:54
 * @Description:
 */
public class HttpServer {
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        httpServer.await();
    }

    private void await() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Constants.DEFAULT_PORT, 1, InetAddress.getByName(Constants.DEFAULT_IP));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!shutdown) {
            Socket socket = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                Request request = new Request(inputStream);
                request.parse();
                Response response = new Response(outputStream);
                response.setRequest(request);
                /**
                 * servlet
                 */
                if (request.getUri().startsWith("/servlet")) {
                    ServletProcessor processor = new ServletProcessor();
                    processor.process(request, response);
                } else {
                    /**
                     * 静态资源
                     */
                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request,response);
                }
                /**
                 * 断开握手
                 */
                socket.close();
                shutdown = request.getUri().equals(Constants.SHUTDOWN_COMMAND);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
