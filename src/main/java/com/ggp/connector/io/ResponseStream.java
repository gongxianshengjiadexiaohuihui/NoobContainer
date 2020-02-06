package com.ggp.connector.io;

import com.ggp.connector.http.HttpResponse;
import org.apache.catalina.util.StringManager;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ggp
 * @Date 2020/2/5 12:38
 * @Description 如果关联的HttpResponse中的contentLength已经设置
 * ResponseStream在写入字节的时候，会限制写入长度不能超过contentLength
 */
public class ResponseStream extends ServletOutputStream {
    /**
     * 标记ResponseStream是否被关闭
     */
    protected boolean closed = false;
    /**
     * 标记刷新时是否应该提交响应
     */
    protected boolean commit = false;
    /**
     * 已经被写入ResponseStream中的字节数量
     */
    protected int count = 0;
    /**
     * 响应内容的长度
     */
    protected int length = -1;
    /**
     * 被关联的HttpResponse
     */
    protected HttpResponse httpResponse = null;
    /**
     * 我们应该写入数据的基础输出流->socket.getOutputStream
     */
    protected OutputStream outputStream = null;
    protected static StringManager stringManager = StringManager.getManager("com.ggp.connector.io");

    public ResponseStream(HttpResponse httpResponse) {
        super();
        this.httpResponse = httpResponse;
    }

    public boolean isCommit() {
        return commit;
    }

    public void setCommit(boolean commit) {
        this.commit = commit;
    }

    /**
     * 关闭流，关闭流前将缓存区数据全部写入
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException(stringManager.getString("responseStream.close.closed"));
        }
        httpResponse.flushBuffer();
        closed = true;
    }

    /**
     * 将缓存中数据写入流中
     *
     * @throws IOException
     */
    @Override
    public void flush() throws IOException {
        if (closed) {
            throw new IOException(stringManager.getString("responseStream.flush.closed"));
        }
        if (commit) {
            httpResponse.flushBuffer();
        }
    }

    /**
     * 写入指定字节
     * @param b
     * @throws IOException
     */
    @Override
    public void write(int b) throws IOException {
        if (closed) {
            throw new IOException(stringManager.getString("responseStream.write.closed"));
        }
        /**
         * 判断写入字符是否已经超过指定长度
         */
        if (length > 0 && count >= length) {
            throw new IOException(stringManager.getString("responseStream.write.count"));

        }
        httpResponse.write(b);
        count++;
    }

    /**
     * 写入一个数组的字节
     * @param bytes
     * @throws IOException
     */
    @Override
    public void write(byte[] bytes) throws IOException{
        write(bytes,0,bytes.length);
    }

    /**
     * 写入数组的指定长度
     * @param bytes
     * @param off
     * @param len
     * @throws IOException
     */
    @Override
    public void write(byte[] bytes,int off, int len) throws IOException{
        int actuallyLen = len;
        if (closed) {
            throw new IOException(stringManager.getString("responseStream.write.closed"));
        }
        if(length>0&&(count+len)>length){
            actuallyLen = length - count;
        }
        httpResponse.write(bytes,off,actuallyLen);
        count+=actuallyLen;
        //todo
        if(actuallyLen<len){
            throw new IOException("responseStream.write.count");
        }
    }

    /**
     * friend  权限
     */
    boolean isClosed(){
        return closed;
    }
    void reset(){
        count = 0
    }
}
