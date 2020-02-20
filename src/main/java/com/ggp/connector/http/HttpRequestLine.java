package com.ggp.connector.http;

/**
 * @author ggp
 * @Date 2020/2/1 20:58
 * @Description 请求行
 */
public class HttpRequestLine {
    /**
     * constants
     */
    public static final int INITIAL_METHOD_SIZE = 8;
    public static final int INITIAL_URI_SIZE = 64;
    public static final int INITIAL_PROTOCOL_SIZE = 8;
    public static final int MAX_METHOD_SIZE = 1024;
    public static final int MAX_URI_SIZE = 32768;
    public static final int MAX_PROTOCOL_SIZE = 1024;

    /**
     * end标识该数组的最后一个字符对应的下标
     */
    public char[] method;
    public int methodEnd;
    public char[] uri;
    public int uriEnd;
    public char[] protocol;
    public int protocolEnd;

    public HttpRequestLine(){
        this(new char[INITIAL_METHOD_SIZE],0,new char[INITIAL_URI_SIZE],0,new char[INITIAL_PROTOCOL_SIZE],0);
    }
    public HttpRequestLine(char[] method,int methodEnd,char[] uri,int uriEnd,char[] protocol,int protocolEnd){
        this.method = method;
        this.methodEnd = methodEnd;
        this.uri = uri;
        this.uriEnd = uriEnd;
        this.protocol = protocol;
        this.protocolEnd = protocolEnd;
    }

    /**
     * 重置
     */
    public void recycle(){
        methodEnd =0;
        uriEnd = 0;
        protocolEnd = 0;
    }

    /**
     * 测试uri中是否包含buf
     * @param buf
     * @return
     */
    public int indexOf(char[] buf){
        return indexOf(buf,buf.length);
    }
    public int indexOf(char[] buf,int end){
        char firstChar = buf[0];
        /**
         * pos是起始位置
         */
        int pos = 0;
        while(pos < uriEnd){
            pos = indexOf(firstChar,pos);
            if(pos == -1){
                return -1;
            }
            if((uriEnd - pos)<end){
                return -1;
            }
            for (int i = 0; i <end ; i++) {
                if(uri[i+pos]!=buf[i]){
                    break;
                }
                if(i == end -1){
                    return pos;
                }
                pos++;
            }
        }
        return -1;
    }
    public int indexOf(String str){
        return indexOf(str.toCharArray(),str.length());
    }

    public int indexOf(char c,int start){
        for (int i = 0; i <start ; i++) {
            if(uri[i] == c){
                return i;
            }
        }
        return -1;
    }
    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}

