package com.ggp.connector.http;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ggp.common.Constants;
import com.ggp.connector.io.ResponseStream;
import com.ggp.connector.io.ResponseWriter;
import org.apache.catalina.util.CookieTools;

/**
 * @author ggp
 * @Date 2020/2/1 19:11
 * @Description
 */
public class HttpResponse implements HttpServletResponse {
    /**
     * 默认的缓冲区大小
     */
    private static final int BUFFER_SIZE = 1024;
    /**
     * 关联的Request
     */
    private HttpRequest request;
    /**
     * 基础输出流
     */
    private OutputStream outputStream;
    /**
     * 输出writer
     */
    private PrintWriter writer;
    /**
     * 缓冲区
     */
    protected byte[] buffer = new byte[BUFFER_SIZE];
    /**
     * 缓冲区写入字节数
     */
    protected int bufferCount = 0;
    /**
     * 标记response是否已经提交
     */
    protected boolean commit = false;
    /**
     * 实际写入Response响应内容中的字节数
     */
    protected int contentCount = 0;
    /**
     * 响应内容的长度
     */
    protected int contentLength = -1;
    /**
     * 响应内容类型
     */
    protected String contentType = null;
    /**
     * 指定字符集
     */
    protected String encoding = null;
    /**
     * cookie
     */
    protected ArrayList cookies = new ArrayList();
    /**
     * Http头部
     */
    protected HashMap headers = new HashMap();
    /**
     * 解析dateHeader
     */
    protected final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.CHINA);
    /**
     * 错误消息
     */
    protected String message = getStatusMessage(HttpServletResponse.SC_OK);
    /**
     * 状态码
     */
    protected int status = HttpServletResponse.SC_OK;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    //todo
    public void finishResponse() {
        if (null != writer) {
            writer.flush();
            writer.close();
        }
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    protected String getProtocol() {
        return request.getProtocol();
    }

    /**
     * 返回状态码对应的信息
     *
     * @param status
     * @return
     */
    protected String getStatusMessage(int status) {
        switch (status) {
            case SC_OK:
                return ("OK");
            case SC_ACCEPTED:
                return ("Accepted");
            case SC_BAD_GATEWAY:
                return ("Bad Gateway");
            case SC_BAD_REQUEST:
                return ("Bad Request");
            case SC_CONFLICT:
                return ("Conflict");
            case SC_CONTINUE:
                return ("Continue");
            case SC_CREATED:
                return ("Created");
            case SC_EXPECTATION_FAILED:
                return ("Expectation Failed");
            case SC_FORBIDDEN:
                return ("Forbidden");
            case SC_GATEWAY_TIMEOUT:
                return ("Gateway Timeout");
            case SC_GONE:
                return ("Gone");
            case SC_HTTP_VERSION_NOT_SUPPORTED:
                return ("HTTP Version Not Supported");
            case SC_INTERNAL_SERVER_ERROR:
                return ("Internal Server Error");
            case SC_LENGTH_REQUIRED:
                return ("Length Required");
            case SC_METHOD_NOT_ALLOWED:
                return ("Method Not Allowed");
            case SC_MOVED_PERMANENTLY:
                return ("Moved Permanently");
            case SC_MOVED_TEMPORARILY:
                return ("Moved Temporarily");
            case SC_MULTIPLE_CHOICES:
                return ("Multiple Choices");
            case SC_NO_CONTENT:
                return ("No Content");
            case SC_NON_AUTHORITATIVE_INFORMATION:
                return ("Non-Authoritative Information");
            case SC_NOT_ACCEPTABLE:
                return ("Not Acceptable");
            case SC_NOT_FOUND:
                return ("Not Found");
            case SC_NOT_IMPLEMENTED:
                return ("Not Implemented");
            case SC_NOT_MODIFIED:
                return ("Not Modified");
            case SC_PARTIAL_CONTENT:
                return ("Partial Content");
            case SC_PAYMENT_REQUIRED:
                return ("Payment Required");
            case SC_PRECONDITION_FAILED:
                return ("Precondition Failed");
            case SC_PROXY_AUTHENTICATION_REQUIRED:
                return ("Proxy Authentication Required");
            case SC_REQUEST_ENTITY_TOO_LARGE:
                return ("Request Entity Too Large");
            case SC_REQUEST_TIMEOUT:
                return ("Request Timeout");
            case SC_REQUEST_URI_TOO_LONG:
                return ("Request URI Too Long");
            case SC_REQUESTED_RANGE_NOT_SATISFIABLE:
                return ("Requested Range Not Satisfiable");
            case SC_RESET_CONTENT:
                return ("Reset Content");
            case SC_SEE_OTHER:
                return ("See Other");
            case SC_SERVICE_UNAVAILABLE:
                return ("Service Unavailable");
            case SC_SWITCHING_PROTOCOLS:
                return ("Switching Protocols");
            case SC_UNAUTHORIZED:
                return ("Unauthorized");
            case SC_UNSUPPORTED_MEDIA_TYPE:
                return ("Unsupported Media Type");
            case SC_USE_PROXY:
                return ("Use Proxy");
            case 207:
                return ("Multi-Status");
            case 422:
                return ("Unprocessable Entity");
            case 423:
                return ("Locked");
            case 507:
                return ("Insufficient Storage");
            default:
                return ("HTTP Response Status " + status);
        }
    }

    public OutputStream getStream() {
        return this.outputStream;
    }

    /**
     * 往响应流中写入头部信息
     */
    protected void sendHeaders() {
        if (isCommitted()) {
            return;
        }
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(getStream(), getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            outputStreamWriter = new OutputStreamWriter(getStream());
        }
        final PrintWriter printWriter = new PrintWriter(outputStreamWriter);
        printWriter.print(this.getProtocol());
        printWriter.print(" ");
        printWriter.print(status);
        if (null != message) {
            printWriter.print(" ");
            printWriter.print(message);
        }
        printWriter.print("\r\n");
        if (null != getContentType()) {
            printWriter.print("Content-Type: " + getContentType() + "\r\n");
        }
        if (getContentLength() >= 0) {
            printWriter.print("Content-Length: " + getContentLength() + "\r\n");
        }
        /**
         * 写入header
         */
        synchronized (headers) {
            Iterator names = headers.keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                ArrayList values = (ArrayList) headers.get(name);
                Iterator iterator = values.iterator();
                while (iterator.hasNext()) {
                    String value = (String) iterator.next();
                    printWriter.print(name);
                    printWriter.print(": ");
                    printWriter.print(value);
                    printWriter.print("\r\n");
                }
            }
        }
        /**
         * 写入cookie
         */
        synchronized (cookies) {
            Iterator items = cookies.iterator();
            while (items.hasNext()) {
                Cookie cookie = (Cookie) items.next();
                printWriter.print(CookieTools.getCookieHeaderName(cookie));
                printWriter.print(": ");
                printWriter.print(CookieTools.getCookieHeaderValue(cookie));
                printWriter.print("\r\n");
            }
        }
        /**
         * 标记头部结束
         */
        printWriter.print("\r\n");
        printWriter.flush();
        commit = true;
    }

    public void setRequest(HttpRequest httpRequest) {
        this.request = httpRequest;
    }

    /**
     * 解析静态资源
     *
     * @throws IOException
     */
    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            File file = new File(Constants.WEB_ROOT, request.getRequestURI());
            fis = new FileInputStream(file);
            int len;
            while ((len = fis.read(bytes, 0, BUFFER_SIZE)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        } catch (FileNotFoundException e) {
            outputStream.write(Constants.ERROR_MESSAGE_404.getBytes());
        } finally {
            if (null != fis) {
                fis.close();
            }
        }
        outputStream.flush();
    }

    /**
     * 写到缓存中，缓存写满写入流中
     *
     * @param b
     * @throws IOException
     */
    public void write(int b) throws IOException {
        if (bufferCount > buffer.length) {
            flushBuffer();
        }
        buffer[bufferCount++] = (byte) b;
        contentCount++;
    }

    /**
     * 写入一个字节数组
     *
     * @param bytes
     * @throws IOException
     */
    public void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    /**
     * 写入指定字节
     *
     * @param bytes
     * @param off
     * @param len
     */
    public void write(byte[] bytes, int off, int len) throws IOException {
        /**
         * 如果写入长度合适，直接写入，如果不合适分批写入
         */
        if (len == 0) {
            return;
        }
        if (len <= (buffer.length - bufferCount)) {
            System.arraycopy(bytes, off, buffer, bufferCount, len);
            bufferCount += len;
            contentCount += len;
            return;
        }
        /**
         * 将缓冲区的数据写入，然后开始以缓冲区为单位写入
         */
        flushBuffer();
        int i = len / buffer.length;
        int offset = i * buffer.length;
        int left = len - offset;
        for (int j = 0; j < i; j++) {
            write(bytes, off + j * buffer.length, buffer.length);
        }
        if (left > 0) {
            write(bytes, off + offset, left);
        }
    }
    public void addCookie(Cookie cookie){
        if(isCommitted()){
            return;
        }
        synchronized (cookies){
            cookies.add(cookie);
        }
    }
    public void addDateHeader(String name,long value){
        if(isCommitted()){
            return;
        }
        addHeader(name,format.format(new Date(value)));
    }
    public void addHeader(String name,String value){
        if(isCommitted()){
            return;
        }
        synchronized (headers){
            ArrayList values = (ArrayList) headers.get(name);
            if(null == values){
                values = new ArrayList();
                headers.put(name,values);
            }
            values.add(value);
        }
    }
    public void addIntHeader(String name, int value) {
        if (isCommitted()){
            return;
        }
        addHeader(name, "" + value);
    }
    public boolean containsHeader(String name) {
        synchronized (headers) {
            return (headers.get(name)!=null);
        }
    }

    public String encodeRedirectURL(String url) {
        return null;
    }

    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    public String encodeUrl(String url) {
        return encodeURL(url);
    }

    public String encodeURL(String url) {
        return null;
    }
    public void flushBuffer() throws IOException{
        if(bufferCount > 0){
            try {
                outputStream.write(buffer,0,bufferCount);
            } finally {
                bufferCount = 0;
            }
        }
    }
    public String getCharacterEncoding() {
        if (null == encoding){
            return Constants.DEFAULT_CHARACTER_ENCODING;
        }
        else{
            return encoding;
        }
    }

    public Locale getLocale() {
        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
    public PrintWriter getWriter() throws IOException{
        ResponseStream responseStream = new ResponseStream(this);
        responseStream.setCommit(false);
        writer = new PrintWriter(new ResponseWriter(new OutputStreamWriter(responseStream,getCharacterEncoding())));
        return writer;
    }

    public void setCharacterEncoding(String charset) {
        encoding = charset;
    }

    public boolean isCommitted() {
        return commit;
    }

    public void reset() {
    }

    public void resetBuffer() {
    }

    public void sendError(int sc) throws IOException {
    }

    public void sendError(int sc, String message) throws IOException {
    }

    public void sendRedirect(String location) throws IOException {
    }

    public void setBufferSize(int size) {
    }

    public int getBufferSize() {
        return 0;
    }

    public void setContentLength(int length) {
        if (isCommitted()){
            return;
        }
        this.contentLength = length;
    }

    public void setContentType(String type) {
        this.contentType = type;
    }
    public void setDateHeader(String name, long value) {
        if (isCommitted()){
            return;
        }
        setHeader(name, format.format(new Date(value)));
    }
    public void setHeader(String name,String value){
        if(isCommitted()){
            return;
        }
        ArrayList values = new ArrayList();
        values.add(value);
        synchronized (headers){
            headers.put(name,values);
        }
        String match = name.toLowerCase();
        if(match.equals("content-length")){
            int contentLength = -1;
            try {
                contentLength = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if(contentLength >= 0){
                setContentLength(contentLength);
            }
        }else if(match.equals("content-type")){
            setContentType(value);
        }
    }
    public void setIntHeader(String name,int value){
      if(isCommitted()){
          return;
      }
      setHeader(name,""+value);
    }
    public void setLocale(Locale locale){
      if(isCommitted()){
          return;
      }
      String language = locale.getLanguage();
      if(null != language && language.length()>0){
          String country = locale.getCountry();
          StringBuffer value = new StringBuffer(language);
          if(null != country && country.length()>0){
              value.append("-");
              value.append(country);
          }
          setHeader("Content-Language",value.toString());
      }
    }
    public void setStatus(int sc) {
    }

    public void setStatus(int sc, String message) {
    }
}
