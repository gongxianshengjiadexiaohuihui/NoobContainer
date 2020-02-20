package com.ggp.connector.http;

/**
 * @author ggp
 * @Date 2020/2/2 13:23
 * @Description
 * http请求头信息（单个请求头）
 */
public class HttpHeader {
    /**
     * 常量
     */
    public static final int INITIAL_NAME_SIZE = 32;
    public static final int INITIAL_VALUE_SIZE = 64;
    public static final int MAX_NAME_SIZE = 128;
    public static final int MAX_VALUE_SIZE = 4096;

    /**
     * 类实例变量
     */
    public char[] name;
    public int nameEnd;
    public char[] value;
    public int valueEnd;
    protected int hashCode = 0;

    /**
     * 构造函数
     */
    public HttpHeader() {
        this(new char[INITIAL_NAME_SIZE], 0, new char[INITIAL_VALUE_SIZE], 0);
    }

    public HttpHeader(String name, String value) {
        this.name = name.toLowerCase().toCharArray();
        this.nameEnd = name.length();
        this.value = value.toCharArray();
        this.valueEnd = value.length();
    }

    public HttpHeader(char[] name, int nameEnd, char[] value, int valueEnd) {
        this.name = name;
        this.nameEnd = nameEnd;
        this.value = value;
        this.valueEnd = valueEnd;
    }

    /**
     * 重置此实例，回收利用
     */
    public void recycle() {
        nameEnd = 0;
        valueEnd = 0;
        hashCode = 0;
    }

    /**
     * 测试参数buf是否和name属性一致
     *
     * @param buf
     * @return
     */
    public boolean equals(char[] buf) {
        return equals(buf, buf.length);
    }

    public boolean equals(char[] buf, int end) {
        if (end != nameEnd) {
            return false;
        }
        for (int i = 0; i < end; i++) {
            if (buf[i] != name[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(String str) {
        return equals(str.toCharArray(), str.length());
    }

    /**
     * 测试buf的值是否和value一致
     *
     * @param buf
     * @return
     */
    public boolean valueEquals(char[] buf) {
        return valueEquals(buf, buf.length);
    }

    public boolean valueEquals(char[] buf, int end) {
        if (end != valueEnd) {
            return false;
        }
        for (int i = 0; i < end; i++) {
            if (buf[i] != value[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean valueEquals(String str) {
        return valueEquals(str.toCharArray(), str.length());
    }

    /**
     * 因为value存在多组值的情况，所以判断value中是否包含buf
     *
     * @param buf
     * @return
     */
    public boolean valueIncludes(char[] buf) {
        return valueIncludes(buf, buf.length);
    }

    /**
     * 很巧妙的一个设计
     *
     * @param buf
     * @param end
     * @return
     */
    public boolean valueIncludes(char[] buf, int end) {
        char firstChar = buf[0];
        int pos = 0;
        while (pos < valueEnd) {
            pos = valueIndexOf(firstChar, pos);
            if (pos == -1) {
                return false;
            }
            if ((valueEnd - pos) < end) {
                return false;
            }
            for (int i = 0; i < end; i++) {
                if (value[i + pos] != buf[i]) {
                    break;
                }
                /**
                 * 说明成立，返回true
                 */
                if (i == (end - 1)) {
                    return true;
                }
            }
            pos++;
        }
        return false;
    }

    public boolean valueIncludes(String str) {
        return valueIncludes(str.toCharArray(), str.length());
    }

    public int valueIndexOf(char c, int start) {
        for (int i = start; i < valueEnd; i++) {
            if (value[i] == c) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断两个头部是否name一致
     *
     * @param header
     * @return
     */
    public boolean equals(HttpHeader header) {
        return equals(header.name, header.nameEnd);
    }

    public boolean headerEquals(HttpHeader header) {
        return equals(header.name, header.nameEnd) && valueEquals(header.value, header.valueEnd);
    }

    @Override
    public int hashCode() {
        int h = hashCode;
        if (h == 0) {
            int off = 0;
            char val[] = name;
            int len = nameEnd;
            for (int i = 0; i < len; i++) {
                h = 31 * h + val[off++];
            }
            hashCode = h;
        }
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return equals(((String) obj).toLowerCase());
        } else if (obj instanceof HttpHeader) {
            return equals((HttpHeader) obj);
        }
        return false;
    }

}
