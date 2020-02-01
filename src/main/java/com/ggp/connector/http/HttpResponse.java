package com.ggp.connector.http;

import java.io.OutputStream;

/**
 * @author ggp
 * @Date 2020/2/1 19:11
 * @Description
 */
public class HttpResponse {
    private HttpRequest request;

    public void setRequest(HttpRequest request) {
        this.request = request;
    }
    public HttpResponse(OutputStream outputStream) {

    }

    public void setHeader(String server, String noob_servlet_container) {

    }
}
