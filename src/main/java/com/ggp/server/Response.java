package com.ggp.server;

import com.ggp.common.Constants;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.*;
import java.util.Locale;

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
public class Response implements ServletResponse {
    private static final int BUFFER_SIZE = 1024;
    private Request request;
    private OutputStream outputStream;
    private PrintWriter writer;
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

    public String getCharacterEncoding() {
        return null;
    }

    public String getContentType() {
        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    public PrintWriter getWriter() throws IOException {
        /**
         * 第二个参数为true,任何 println 方法的调用都会刷新输出(output)。 不过，print 方法不会刷新输出
         */
        writer = new PrintWriter(outputStream,true);
        return writer;
    }

    public void setCharacterEncoding(String s) {

    }

    public void setContentLength(int i) {

    }

    public void setContentType(String s) {

    }

    public void setBufferSize(int i) {

    }

    public int getBufferSize() {
        return 0;
    }

    public void flushBuffer() throws IOException {

    }

    public void resetBuffer() {

    }

    public boolean isCommitted() {
        return false;
    }

    public void reset() {

    }

    public void setLocale(Locale locale) {

    }

    public Locale getLocale() {
        return null;
    }
}
