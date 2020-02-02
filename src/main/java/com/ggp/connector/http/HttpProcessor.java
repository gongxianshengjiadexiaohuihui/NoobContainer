package com.ggp.connector.http;

import com.ggp.server.ServletProcessor;
import com.ggp.server.StaticResourceProcessor;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.StringManager;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author ggp
 * @Date 2020/2/1 18:50
 * @Description
 */
public class HttpProcessor {
    private HttpConnector connector = null;
    private HttpRequest request;
    private HttpResponse response;
    private HttpRequestLine httpRequestLine = new HttpRequestLine();

    /**
     * 错误信息管理
     */
    protected StringManager stringManager = StringManager.getManager("com.ggp.connector.http");

    /**
     * 1、创建一个HttpRequest对象
     * 2、创建一个HttpRespond对象
     * 3、解析HTTP请求头的第一行和头部，并释放HttpRequest对象
     * 4、解析HttpRequest和HttpRespond对象到一个ServletProcessor或者StaticResourceProcessor
     *
     * @param socket
     */
    public void parse(Socket socket) {
        SocketInputStream socketInputStream = null;
        OutputStream outputStream = null;

        try {
            socketInputStream = new SocketInputStream(socket.getInputStream(), 2048);
            outputStream = socket.getOutputStream();
            request = new HttpRequest(socketInputStream);
            response = new HttpResponse(outputStream);
            response.setRequest(request);
            response.setHeader("Server", "Noob Servlet Container");
            parseRequest(socketInputStream, outputStream);
            parseHeader(socketInputStream);
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
                processor.process(request, response);
            }
            /**
             * 断开握手
             */
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseRequest(SocketInputStream socketInputStream, OutputStream outputStream) throws IOException, ServletException {
        /**
         * 填充httpRequestLine实例
         */
        socketInputStream.readRequestLine(httpRequestLine);
        /**
         * 获得请求行的方法
         */
        String method = new String(httpRequestLine.method, 0, httpRequestLine.methodEnd);
        /**
         * 请求行的uri
         */
        String uri = null;
        /**
         * 请求行的协议
         */
        String protocol = new String(httpRequestLine.protocol, 0, httpRequestLine.protocolEnd);
        if (method.length() < 1) {
            throw new ServletException("Missing HTTP request method");
        } else if (httpRequestLine.uriEnd < 1) {
            throw new ServletException("Missing HTTP request uri");
        }
        /**
         * URI后面可以跟查询字符串，假如存在的话，查询字符串会用一个问号隔开，因此首先尝试获取查询字符串。并填充
         * HttpRequest中的QueryString属性。
         */
        int question = httpRequestLine.indexOf("?");
        if (question >= 0) {
            request.setQueryString(new String(httpRequestLine.uri, question + 1, httpRequestLine.uriEnd - question - 1));
            uri = new String(httpRequestLine.uri, 0, question);
        } else {
            request.setQueryString(null);
            uri = new String(httpRequestLine.uri, 0, httpRequestLine.uriEnd);
        }
        /**
         * 大多数情况下，URI指向一个相对资源,URI还可以是一个绝对值，如：
         * http://www.brainysoftware.com/index.html?name=Tarzan
         * 需要兼容这种情况
         */
        if (!uri.endsWith("/")) {
            int pos = uri.indexOf("://");
            if (pos != -1) {
                pos = uri.indexOf("/", pos + 3);
                if (pos == -1) {
                    uri = "";
                } else {
                    uri = uri.substring(pos);
                }
            }
        }
        /**
         *  查询字符串也可以包含一个会话标识符，用 jsessionid 参数名来指代。
         *  因此， parseRequest 方法也检查一个会话标识符。
         *  假如在查询字符串里边找到 jessionid，方法就取得会话标识符，
         *  并通过调用 setRequestedSessionId 方法把值交给 HttpRequest 实例
         *
         *  当 jsessionid 被找到，也意味着会话标识符是携带在查询字符串里边，而不是在 cookie 里边。
         *  因此，传递 true 给 request 的 setRequestSessionURL 方法。
         *  否则，传递 false 给 setRequestSessionURL 方法并传递 null 给 setRequestedSessionURL 方法。
         *  到这个时候，uri 的值已经被去掉了 jsessionid
         */
        String match = ";jsessionid=";
        int semicolon = uri.indexOf(match);
        if (semicolon >= 0) {
            String rest = uri.substring(semicolon + match.length());
            int semicolon2 = rest.indexOf(';');
            if (semicolon2 >= 0) {
                request.setRequestSessionId(rest.substring(0, semicolon2));
                rest = rest.substring(semicolon2);
            } else {
                request.setRequestSessionId(rest);
                rest = "";
            }
            request.setRequestSessionURL(true);
            uri = uri.substring(0, semicolon) + rest;
        } else {
            request.setRequestSessionId(null);
            request.setRequestSessionURL(false);
        }
        /**
         * 如 URI 不能纠正的话，它将会给认为是非法的并且通常会返回 null。
         */
        String normalizedUri = normalize(uri);
        request.setMethod(method);
        request.setProtocol(protocol);
        if (null != normalizedUri) {
            request.setRequestURI(normalizedUri);
        } else {
            request.setRequestURI(uri);
        }
        if (null == normalizedUri) {
            throw new ServletException("Invalid URI: " + uri + "'");
        }
    }

    /**
     * 用于纠正“异常”的 URI。
     * 例如， 任何\的出现都会给/替代。
     * 假如 uri 是正确的格式或者异常可以给纠正的话，normalize 将会返 回相同的或者被纠正后的 URI。
     * 假如 URI 不能纠正的话，它将会给认为是非法的并且通常会返回 null。
     * 在这种情况下(通常返回 null)，parseRequest 将会在方法的最后抛出一个异常
     *
     * @param uri
     * @return
     */
    private String normalize(String uri) {
    }

    private void parseHeader(SocketInputStream socketInputStream) throws IOException, ServletException {
        while (true) {
            HttpHeader httpHeader = new HttpHeader();
            /**
             * 填充HttpHeader实例
             */
            socketInputStream.readHeader(httpHeader);
            /**
             * 通过检测HttpHeader实例的nameEnd和valueEnd来测试是否可以从输入流中读取下一个头部信息
             */
            if (httpHeader.nameEnd == 0){
                 if(httpHeader.valueEnd == 0){
                     /**
                      * 所有的头部属性都被解析完成，跳出循环
                      */
                     return;
                 }else{
                     throw new ServletException(stringManager.getString("httpProcessor.parseHeaders.colon"));
                 }
            }
            /**
             * 存在头部，存储头部的名称和值
             */
            String name = new String(httpHeader.name,0,httpHeader.nameEnd);
            String value = new String(httpHeader.value,0,httpHeader.valueEnd);
            request.addHeader(name,value);
            /**
             * 一些头部也需要某些属性设置，让其可以直接用get获得
             */
            if(name.equals("cookie")){
                //todo
            }else if(name.equals("content-length")){
                int n = -1;
                try {
                    n = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new ServletException(stringManager.getString("httpProcessor.parseHeader.contentLength"));
                }
                request.setContentLength(value);
            }else if(name.equals("content-type")){
                request.setContentType(value);
            }
        }
    }
}
