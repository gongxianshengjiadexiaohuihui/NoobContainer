package com.ggp.server;

/**
 * @author ggp
 * @Date 2020/1/30 18:16
 * @Description
 */
public interface Processor {
    /**
     * 处理方法
     * @param request
     * @param response
     */
    void process(Request request, Response response);
}
