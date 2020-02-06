package com.ggp.connector.io;

import com.ggp.connector.http.HttpRequest;
import org.apache.catalina.util.StringManager;

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ggp
 * @Date 2020/2/6 16:18
 * @Description
 * RequestStream流读取的时候有长度限制，不能超过ContentLength
 */
public class RequestStream extends ServletInputStream {
    /**
     * 标记RequestStream是否被关闭
     */
    protected boolean closed = false;
    /**
     * RequestStream中已经返回的字节数
     */
    protected int count = 0;
    /**
     * 请求内容的长度
     */
    protected int length = -1;
    /**
     * 读取数据的基础流
     */
    protected InputStream inputStream;
    protected static StringManager stringManager = StringManager.getManager("com.ggp.connector.io");

    public RequestStream(HttpRequest httpRequest) {
        super();
        length = httpRequest.getContentLength();
        inputStream = httpRequest.getStream();
    }

    /**
     * 关闭流
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException(stringManager.getString("requestStream.close.closed"));
        }
        if (length > 0) {
            /**
             * 关闭流前，如果已经设置了contentLength，并且并未全部被消耗，就消耗完剩余所有字节
             */
            while (count < length) {
                int b = read();
                if (b < 0) {
                    break;
                }
            }
        }
        closed = true;
    }

    /**
     * 一次读取一个字节并返回，读到文件结尾返回-1
     *
     * @return
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        /**
         * 流是否已经被关闭
         */
        if (closed) {
            throw new IOException(stringManager.getString("requestStream.read.closed"));
        }
        /**
         * 是否已经读到了指定内容长度
         */
        if (length >= 0 && count >= length) {
            return -1;
        }
        int b = inputStream.read();
        if (b > 0) {
            count++;
        }
        return b;
    }

    /**
     * 读取bytes.length长度的字节
     * @param bytes
     * @return
     * @throws IOException
     */
    @Override
    public int read(byte[] bytes) throws IOException{
        return read(bytes,0,bytes.length);
    }

    /**
     * 读取最多长度为len的字节，并从off开始放入bytes中
     * @param bytes
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    @Override
    public int read(byte[] bytes,int off,int len)throws IOException{
        int actuallyReadLength = len;
        /**
         * 判断实际读取的长度
         */
        if(length>0){
            if(count>=length){
                return -1;
            }
            if(count + len > length){
                actuallyReadLength = length - count;
            }
        }
        return super.read(bytes,off,actuallyReadLength);
    }
}
