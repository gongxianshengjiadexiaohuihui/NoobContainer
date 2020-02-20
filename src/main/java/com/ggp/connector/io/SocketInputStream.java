package com.ggp.connector.io;

import com.ggp.connector.http.HttpHeader;
import com.ggp.connector.http.HttpRequestLine;
import com.ggp.connector.http.RequestLineAttribute;
import com.ggp.simplecontainer.Request;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.catalina.util.StringManager;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ggp
 * @Date 2020/2/1 19:12
 * @Description 解析请求行和头部
 */
public class SocketInputStream extends InputStream {
    private static final byte CR = (byte) '\r';
    private static final byte LF = (byte) '\n';
    private static final byte SP = (byte) ' ';
    private static final byte HT = (byte) '\t';
    private static final byte COLON = (byte) ':';
    private static final int LC_OFFSET = 'A' - 'a';
    /**
     * 缓冲区
     */
    protected byte[] buffer;
    /**
     * 缓冲区中实际可用的字节数（因为有时候从基础流中读出的字节并不能塞满缓冲区）
     */
    protected int count;
    /**
     * 缓冲区指针位置
     */
    protected int pos;
    /**
     * 基础流
     */
    protected InputStream inputStream;
    protected static StringManager stringManager = StringManager.getManager("com.ggp.connector.http");

    public SocketInputStream(InputStream inputStream, int buffSize) {
        this.inputStream = inputStream;
        buffer = new byte[buffSize];
    }

    /**
     * 读取指定的内容解析请求行并赋值给参数 requestLine
     *
     * @param requestLine
     */
    public void readRequestLine(HttpRequestLine requestLine) throws IOException {
        /**
         * 保证requestLine重新开始初始化
         */
        if (requestLine.methodEnd != 0) {
            requestLine.recycle();
        }
        /**
         * 跳过空行
         */
        int chr = 0;
        do {
            try {
                chr = read();
            } catch (IOException e) {
                chr = -1;
            }
        } while (chr == CR || chr == LF);
        if (chr == -1) {
            throw new EOFException(stringManager.getString("socketInputStream.readRequestLine.error"));
        }
        /**
         * 因为满足do while条件后，read其实pos++了，让pos回退到读到CRLF后第一个字符
         */
        pos--;

        RequestLineAttribute attribute = new RequestLineAttribute();
        /**
         * 读方法
         */
        attribute.setAttribute(requestLine.method);
        attribute.setMaxSize(HttpRequestLine.MAX_METHOD_SIZE);
        readAttribute(attribute);
        requestLine.method = attribute.attribute;
        requestLine.methodEnd = attribute.attributeEnd;
        /**
         * 读uri
         */
        attribute.setAttribute(requestLine.uri);
        attribute.setMaxSize(HttpRequestLine.MAX_URI_SIZE);
        readAttribute(attribute);
        requestLine.uri = attribute.attribute;
        requestLine.uriEnd = attribute.attributeEnd;
        /**
         * 读协议
         */
        attribute.setAttribute(requestLine.protocol);
        attribute.setMaxSize(HttpRequestLine.MAX_PROTOCOL_SIZE);
        attribute.end = true;
        readAttribute(attribute);
        requestLine.protocol = attribute.attribute;
        requestLine.protocolEnd = attribute.attributeEnd;
    }

    private void readAttribute(RequestLineAttribute attribute) throws IOException {
        /**
         * 初始化长度
         */
        int maxRead = attribute.getAttribute().length;
        /**
         * 属性值数组的下标
         */
        int readCount = 0;
        boolean space = false;

        while (!space) {
            /**
             * 读取的字节数大于初始化长度的时候，以2倍大小扩展
             */
            if (readCount >= maxRead) {
                if (maxRead * 2 <= attribute.getMaxSize()) {
                    char[] newBuffer = new char[maxRead * 2];
                    System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                    attribute.setAttribute(newBuffer);
                } else {
                    throw new IOException(stringManager.getString("socketInputStream.readAttribute.extendError"));
                }
            }
            /**
             * 如果缓冲区数据用完，需要更新
             */
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException(stringManager.getString("socketInputStream.readAttribute.readError"));
                }
                /**
                 * read() 读了一个
                 */
                pos = 0;
            }
            if (buffer[pos] == SP) {
                space = true;
            } else if (buffer[pos] == CR) {
                /**
                 * 跳过
                 */
                pos++;
                continue;
            } else if (buffer[pos] == LF) {
                /**
                 * 指针移动到LF下一个字符
                 */
                pos++;
                /**
                 * 抵消下面的readCount-1，因为 CR和LF都不是有效字符所以不计入readCount，其实 SP CR LF 都经历了pos ++  至于CR LF 比较特殊
                 * CR 直接跳过所以readCount不计数，LF 是跳出所以readCount也不计数，只不过 CR还没有跳出，所以不用readCount++去抵消下面统一的
                 * readCont -1
                 */
                readCount++;
                break;
            }
            attribute.attribute[readCount++] = (char) buffer[pos++];
        }
        /**
         * 抵消上面的readCount++
         * 当space等于true的时候，attribute.attribute[readCount++] = (char)buffer[pos++] 仍然会执行一次，多读一个字符
         */
        attribute.attributeEnd = readCount - 1;
    }

    /**
     * 读取指定内容
     *
     * @param header
     * @throws IOException
     */
    public void readHeader(HttpHeader header) throws IOException {
        /**
         * 保证重新开始初始化
         */
        if (header.nameEnd != 0) {
            header.recycle();
        }
        /**
         * 如果一开始就读到CRLF或LF,说明全部头部已经都被读完了，返回0值
         */
        int chr = read();
        if (chr == CR || chr == LF) {
            if (chr == CR) {
                /**
                 * 跳过LF
                 */
                read();
            }
            header.nameEnd = 0;
            header.valueEnd = 0;
            return;
        } else {
            /**
             * 如果不是CRLF，pos位置回退1，因为read()里面有pos++;
             */
            pos--;
        }

        /**
         * read name
         */
        int maxRead = header.name.length;
        int readCount = 0;
        boolean colon = false;
        while (!colon) {
            if (maxRead * 2 <= HttpHeader.MAX_NAME_SIZE) {
                char[] newBuffer = new char[2 * maxRead];
                System.arraycopy(header.name, 0, newBuffer, 0, maxRead);
                header.name = newBuffer;
                maxRead = header.name.length;
            } else {
                throw new IOException
                        (stringManager.getString("requestStream.readHeader.tooLong"));
            }
            /**
             * 如果缓冲区数据被读完，重新刷新缓冲区
             */
            if (pos > count) {
                int val = read();
                if (val == -1) {
                    throw new IOException(stringManager.getString("requestStream.readHeader.error"));
                }
                /**
                 * read() 读了一个
                 */
                pos = 0;
            }
            /**
             * 循环终止条件
             */
            if (buffer[pos] == COLON) {
                colon = true;
            }
            char val = (char) buffer[pos];
            /**
             * 将大写字母转化为小写字母
             */
            if (val >= 'A' && val <= 'Z') {
                val = (char) (val - LC_OFFSET);
            }
            header.name[readCount++] = val;
            pos++;
        }
        /**
         * 抵消分号COLON
         */
        header.nameEnd = readCount - 1;
        /**
         * read value
         */
        maxRead = header.value.length;
        readCount = 0;
        int crPos = -2;
        /**
         * 标志是否终止，是否读到换行
         */
        boolean eol = false;
        boolean validLine = true;

        while (validLine) {
            boolean space = true;
            /**
             * 跳过前面的空白（空格和TAB）
             */
            while (space) {
                if (pos >= count) {
                    int val = read();
                    if (val == -1) {
                        throw new IOException(stringManager.getString("requestStream.readHeader.error"));
                    }
                    /**
                     * read() 读了一个
                     */
                    pos = 0;
                }
                if (buffer[pos] == SP || buffer[pos] == HT) {
                    pos++;
                } else {
                    space = false;
                }
            }
            while (!eol) {
                if (readCount >= maxRead) {
                    if (maxRead * 2 <= HttpHeader.MAX_VALUE_SIZE) {
                        char[] newBuff = new char[maxRead * 2];
                        System.arraycopy(header.value,0,newBuff,0,maxRead);
                        header.value = newBuff;
                        maxRead = header.value.length;
                    }else{
                        throw new IOException
                                (stringManager.getString("requestStream.readHeader.tooLong"));
                    }
                }
                if (pos >= count) {
                    int val = read();
                    if (val == -1) {
                        throw new IOException(stringManager.getString("requestStream.readHeader.error"));
                    }
                    /**
                     * read() 读了一个
                     */
                    pos = 0;
                }
                if(buffer[pos] == CR){
                    /**
                     * 跳过
                     */
                }else if(buffer[pos] == LF){
                    /**
                     * value已经读完，进入下一行
                     */
                    eol = true;
                }else{
                    header.value[readCount++] = (char)buffer[pos];
                }
                pos ++;
            }
            /**
             * rfc2616
             * HTTP/1.1 header field values can be folded onto multiple lines if the continuation line begins with a space or
             * horizontal tab. All linear white space, including folding, has the same semantics as SP. A recipient MAY replace any
             * linear white space with a single SP before interpreting the field value or forwarding the message downstream.
             * LWS = [CRLF] 1*( SP | HT )
             * value 可能被折叠多行，这些行都是以连续的空格或tab开头，如果是这样读取的时候或者传递给下层时，行头这些连续空白用一个SP（空格）
             * 代替
             */
            int nextChr = read();
            if(nextChr != SP && nextChr != HT){
                pos--;
                validLine = false;
            }else{
                eol =false;
                if (readCount >= maxRead) {
                    if (maxRead * 2 <= HttpHeader.MAX_VALUE_SIZE) {
                        char[] newBuff = new char[maxRead * 2];
                        System.arraycopy(header.value,0,newBuff,0,maxRead);
                        header.value = newBuff;
                        maxRead = header.value.length;
                    }else{
                        throw new IOException
                                (stringManager.getString("requestStream.readHeader.tooLong"));
                    }
                }
                header.value[readCount++] = SP;
            }
        }
        header.valueEnd = readCount;

    }


    @Override
    public int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count) {
                return -1;
            }
        }
        return buffer[pos++] & 0xff;
    }

    /**
     * 重置缓冲区，并从基础流中读出数据放入缓冲区。第一次也会重置
     *
     * @throws IOException
     */
    protected void fill() throws IOException {
        pos = 0;
        count = 0;
        int nRead = inputStream.read(buffer, 0, buffer.length);
        if (nRead > 0) {
            count = nRead;
        }
    }
}
