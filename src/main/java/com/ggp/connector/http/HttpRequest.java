package com.ggp.connector.http;

import com.ggp.common.Constants;
import com.ggp.connector.io.RequestStream;
import com.ggp.connector.io.SocketInputStream;
import org.apache.catalina.util.ParameterMap;
import org.apache.catalina.util.RequestUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.util.*;

/**
 * @author ggp
 * @Date 2020/2/1 19:10
 * @Description HTTP请求解析
 * 1、读取套接字的输入流
 * 2、解析请求行
 * 3、解析cookies
 * 4、获取参数
 */
public class HttpRequest implements HttpServletRequest {
    /**
     * 请求的内容类型
     */
    private String contentType;
    /**
     * 请求内容长度
     */
    private int contentLength;
    private InetAddress inetAddress;
    /**
     * 输入流
     */
    private InputStream inputStream;
    /**
     * 请求方法 位于请求行
     */
    private String method;
    /**
     * 请求协议 位于请求行
     */
    private String protocol;
    /**
     * 查询字符串 位于请求行
     */
    private String queryString;
    /**
     * 请求uri 位于请求行
     */
    private String requestURI;
    private String serverName;
    private int serverPort;
    private Socket socket;
    private boolean requestedSessionCookie;
    private String requestedSessionId;
    private boolean requestedSessionURL;

    /**
     * 请求属性
     */
    protected HashMap attributes = new HashMap();
    /**
     * 与此请求一同发送的授权凭证
     */
    protected String authorization = null;
    /**
     * 此请求的上下文路径
     */
    protected String contextPath = "";
    /**
     * 头部
     */
    protected HashMap<String,String> headers = new HashMap();
    /**
     * cookies
     */
    protected ArrayList cookies = new ArrayList();
    /**
     * 参数
     */
    protected ParameterMap parameters = null;
    /**
     * 判断是否已经解析过参数
     */
    protected boolean parsed = false;
    protected BufferedReader reader = null;
    protected ServletInputStream servletInputStream = null;

    public HttpRequest(InputStream inputStream) {
         this.inputStream = inputStream;
    }
    //todo 有争议

    /**
     * 添加头部属性
     * @param name
     * @param value
     */
    public void addHeader(String name,String value){
         name = name.toLowerCase();
         synchronized (headers){
             String result = headers.get(name);
             if(null == result){
                 headers.put(name,value);
             }
         }
    }

    /**
     * 添加cookie
     * @param cookie
     */
    public void addCookie(Cookie cookie){
        synchronized (cookies){
            cookies.add(cookie);
        }
    }
    /**
     * 解析参数，参数可能存在于查询字符串或者是请求内容中
     */
    public void parseParameters() {
        /**
         * 如果已经解析过，就可以直接从parameters中获取
         */
        if (parsed) {
            return;
        }
        ParameterMap results = parameters;
        if (null == results) {
            results = new ParameterMap();
        }
        /**
         * 当 locked 是 false 的时候，可以添加，更新或者移除。
         */
        results.setLocked(false);
        /**
         * 检查字符编码，当字符编码为null的时候，赋值为默认编码。
         */
        String encoding = getCharacterEncoding();
        if (null == encoding) {
            encoding = Constants.DEFAULT_CHARACTER_ENCODING;
        }
        /**
         * 解析查询字符串中的参数
         */
        String queryString = getQueryString();
        try {
            RequestUtil.parseParameters(results, queryString, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /**
         * 解析请求内容中参数，这种情况发生在请求方法是POST,内容长度大于0，并且
         * 内容类型是 application/x-www-form-urlencoded 的时候
         */
        String contentType = getContentType();
        if (null == contentType) {
            contentType = "";
        }
        int semicolon = contentType.indexOf(';');
        /**
         * 例如Content-Type: application/json; charset=utf-8
         */
        if (semicolon >= 0) {
            contentType = contentType.substring(0, semicolon).trim();
        } else {
            contentType = contentType.trim();
        }
        if ("POST".equals(getMethod()) && getContentLength() > 0 && "application/x-www-form-urlencoded".equals(contentType)) {
            try {
                int max = getContentLength();
                int len = 0;
                byte[] buffer = new byte[max];
                ServletInputStream inputStream = getInputStream();
                while (len < max) {
                    /**
                     * read 的三个参数第一个是读取后放入的字节数组，第二个参数是放入数组的起点，第三个参数是读取并放入的长度。
                     */
                    int next = inputStream.read(buffer, len, max - len);
                    if (next < 0) {
                        break;
                    }
                    len += next;
                }
                inputStream.close();
                if (len < max) {
                    throw new RuntimeException("Content length mismatch");
                }
                RequestUtil.parseParameters(results, buffer, encoding);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Content read fail");
            }
        }
        //todo  多线程会有问题
        /**
         * 锁定结果
         */
        results.setLocked(true);
        parsed = true;
        parameters = results;
    }
    public ServletInputStream createServletInputStream(){
        return (new RequestStream(this));
    }
    public InputStream getStream(){
        return this.inputStream;
    }
    /**
     * 下面是继承HttpServletRequest的方法
     */
    public String getAuthType() {
        return null;
    }

    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    public long getDateHeader(String s) {
        return 0;
    }

    public String getHeader(String s) {
        return null;
    }

    public Enumeration getHeaders(String s) {
        return null;
    }

    public Enumeration getHeaderNames() {
        return null;
    }

    public int getIntHeader(String s) {
        return 0;
    }

    public String getMethod() {
        return null;
    }

    public String getPathInfo() {
        return null;
    }

    public String getPathTranslated() {
        return null;
    }

    public String getContextPath() {
        return null;
    }

    public String getQueryString() {
        return null;
    }

    public String getRemoteUser() {
        return null;
    }

    public boolean isUserInRole(String s) {
        return false;
    }

    public Principal getUserPrincipal() {
        return null;
    }

    public String getRequestedSessionId() {
        return null;
    }

    public String getRequestURI() {
        return null;
    }

    public StringBuffer getRequestURL() {
        return null;
    }

    public String getServletPath() {
        return null;
    }

    public HttpSession getSession(boolean b) {
        return null;
    }

    public HttpSession getSession() {
        return null;
    }

    public boolean isRequestedSessionIdValid() {
        return false;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
        return false;
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
