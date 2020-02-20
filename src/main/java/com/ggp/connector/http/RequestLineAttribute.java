package com.ggp.connector.http;

/**
 * @author ggp
 * @Date 2020/2/12 10:20
 * @Description
 * 整合相同属性，方便读取解析
 */
public class RequestLineAttribute {
    public int initSize;
    public int maxSize;
    public char[] attribute;
    public int attributeEnd;
    /**
     * 标志读到请求行最后一项，也就是协议
     */
    public boolean end =false;

    public int getInitSize() {
        return initSize;
    }

    public void setInitSize(int initSize) {
        this.initSize = initSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public char[] getAttribute() {
        return attribute;
    }

    public void setAttribute(char[] attribute) {
        this.attribute = attribute;
    }

    public int getAttributeEnd() {
        return attributeEnd;
    }

    public void setAttributeEnd(int attributeEnd) {
        this.attributeEnd = attributeEnd;
    }
}
