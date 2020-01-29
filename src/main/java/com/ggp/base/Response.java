package com.ggp.base;

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

    public Response(OutputStream outputStream) {

    }

    public void setRequest(Request request) {
    }

    public void sendStaticResource() {
    }
}
