package com.ggp.server;

import com.ggp.common.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ggp
 * @Date 2020/1/29 20:07
 * @Description 一个 HTTP 响应也包括三个组成部分：
 * * 方法—统一资源标识符(URI)—协议/版本
 * * 响应的头部
 * * 主体内容
 * 例如：
 * 1 HTTP/1.1 200 OK
 * 2 Server: Microsoft-IIS/4.0
 * 3 Date: Mon, 5 Jan 2004 13:13:33 GMT
 * 4 Content-Type: text/html
 * 5 Last-Modified: Mon, 5 Jan 2004 13:13:12 GMT
 * 6 Content-Length: 112
 * 7
 * 8  <html>
 * 9     <head>
 * 10         <title>HTTP Response Example</title>
 * 11     </head>
 * 12     <body> Welcome to Brainy Software </body>
 * 13  </html>
 * 参考 @Request
 */
public class Response {
    private static final int BUFFER_SIZE = 1024;
    private Request request;
    private OutputStream outputStream;
    public Response(OutputStream outputStream) {
       this.outputStream = outputStream;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * 根据uri读取对应的静态资源
     * @throws IOException
     */
    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        File file = new File(Constants.WEB_ROOT,request.getUri());
        try {
            if(file.exists()){
                fis = new FileInputStream(file);
                int ch;
                while ((ch = fis.read(bytes,0,BUFFER_SIZE))!=-1){
                    outputStream.write(bytes);
                }
            }else{
                outputStream.write(Constants.ERROR_MESSAGE_404.getBytes());
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != fis){
                fis.close();
            }
        }
    }
}
