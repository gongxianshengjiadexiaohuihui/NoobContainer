package com.ggp.simplecontainer;

import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @author ggp
 * @Date 2020/1/29 20:07
 * @Description 一个HTTP请求包括三部分：
 * *方法—统一资源标识符(URI)—协议/版本
 * *请求的头部
 * *主体内容
 * 例如：
 * begin
 * 1 POST /examples/default.jsp HTTP/1.1
 * 2 Accept: text/plain; text/html
 * 3 Accept-Language: en-gb
 * 4 Connection: Keep-Alive
 * 5 Host: localhost
 * 6 User-Agent: Mozilla/4.0 (compatible; MSIE 4.01; Windows 98)
 * 7 Content-Length: 33
 * 8 Content-Type: application/x-www-form-urlencoded
 * 9 Accept-Encoding: gzip, deflate
 * 10
 * 11 lastName=Franks&firstName=Michael
 * end
 * * 方法—统一资源标识符(URI)—协议/版本出现在请求的第一行。 POST是请求方法，/examples/default.jsp 是URI,HTTP/1.1是协议版本部分。
 * 扩充：
 * HTTP1.1支持7种类型的请求：GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE。
 * URI 完全指明了一个互联网资源。URI 通常是相对服务器的根目录解释的。因此，始终一斜 线/开头
 * URI = Universal Resource Identifier 统一资源标志符  标识
 * URL = Universal Resource Locator 统一资源定位符     定位，是一个可访问地址
 * <p>
 * * 请求头部是2到9行，可以有多个，key-value形式，每个头部用换行符(CRLF)隔开
 * <p>
 * *主体内容和头部之间用换行符(CRLF)隔开
 */
public class Request implements ServletRequest {
    private Logger logger = Logger.getLogger(this.getClass());
    private InputStream inputStream;
    private String uri;

    public Request(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * 处理request中的原始数据
     */
    public void parse() {
        StringBuffer stringBuffer = new StringBuffer(2048);
        byte[] buffer = new byte[2048];
        int i;
        try {
            i = inputStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for (int j = 0; j < i; j++) {
            stringBuffer.append((char) buffer[j]);
        }
        logger.debug("request获取的原始数据:" + stringBuffer.toString());
        uri = parseUri(stringBuffer.toString());
    }

    /**
     * 获取uri   从第一行解析获取     方法  uri  协议/版本
     * @param src
     * @return
     */
    private String parseUri(String src) {
        int index1, index2;
        index1 = src.indexOf(' ');
        if (index1 != -1) {
            index2 = src.indexOf(' ', index1 + 1);
            if (index2 > index1) {
                return src.substring(index1 + 1, index2);
            }
        }
        return null;
    }

    public String getUri() {
        return uri;
    }

    public Object getAttribute(String s) {
        return null;
    }

    public Enumeration getAttributeNames() {
        return null;
    }

    public String getCharacterEncoding() {
        return null;
    }

    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    public int getContentLength() {
        return 0;
    }

    public String getContentType() {
        return null;
    }

    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    public String getParameter(String s) {
        return null;
    }

    public Enumeration getParameterNames() {
        return null;
    }

    public String[] getParameterValues(String s) {
        return new String[0];
    }

    public Map getParameterMap() {
        return null;
    }

    public String getProtocol() {
        return null;
    }

    public String getScheme() {
        return null;
    }

    public String getServerName() {
        return null;
    }

    public int getServerPort() {
        return 0;
    }

    public BufferedReader getReader() throws IOException {
        return null;
    }

    public String getRemoteAddr() {
        return null;
    }

    public String getRemoteHost() {
        return null;
    }

    public void setAttribute(String s, Object o) {

    }

    public void removeAttribute(String s) {

    }

    public Locale getLocale() {
        return null;
    }

    public Enumeration getLocales() {
        return null;
    }

    public boolean isSecure() {
        return false;
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    public String getRealPath(String s) {
        return null;
    }

    public int getRemotePort() {
        return 0;
    }

    public String getLocalName() {
        return null;
    }

    public String getLocalAddr() {
        return null;
    }

    public int getLocalPort() {
        return 0;
    }
}
