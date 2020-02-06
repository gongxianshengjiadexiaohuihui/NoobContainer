package com.ggp.simplecontainer;

import java.io.IOException;

/**
 * @author ggp
 * @Date 2020/1/30 18:13
 * @Description 处理静态资源请求
 */
public class StaticResourceProcessor implements Processor {
    public void process(Request request, Response response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
