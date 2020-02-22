package com.ggp.connector.processor;

import com.ggp.connector.http.HttpRequest;
import com.ggp.connector.http.HttpResponse;

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
    void process(HttpRequest request, HttpResponse response);
}
