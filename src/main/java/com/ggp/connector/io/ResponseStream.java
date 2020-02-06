package com.ggp.connector.io;

import com.ggp.connector.http.HttpResponse;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ggp
 * @Date 2020/2/5 12:38
 * @Description
 */
public class ResponseStream extends ServletOutputStream {
    /**
     * 标记ResponseStream是否被关闭
     */
    protected boolean closed = false;
    /**
     * 标记刷新后是否应该提交响应
     */
    protected boolean commit = false;
    /**
     * 已经被写入ResponseStream中的字节数量
     */
    protected int count = 0;
    /**
     *
     */
    protected int length= -1;
    /**
     * 被关联的HttpResponse
     */
    protected HttpResponse httpResponse = null;
    /**
     * 我们应该写入数据的基础输出流->socket.getOutputStream
     */
    protected OutputStream outputStream = null;

    public ResponseStream(HttpResponse httpResponse){
        super();
        this.httpResponse = httpResponse;
    }

    public boolean isCommit() {
        return commit;
    }

    public void setCommit(boolean commit) {
        this.commit = commit;
    }


    @Override
    public void write(int b) throws IOException {

    }
}
