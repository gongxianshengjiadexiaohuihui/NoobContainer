package com.ggp.connector.processor;

import com.ggp.common.Constants;
import com.ggp.connector.http.HttpRequest;
import com.ggp.connector.http.HttpResponse;
import com.ggp.simplecontainer.*;

import javax.servlet.Servlet;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * @author ggp
 * @Date 2020/1/30 18:09
 * @Description 处理servlet请求
 */
public class ServletProcessor implements Processor {
    public void process(HttpRequest request, HttpResponse response) {
        String uri = request.getRequestURI();
        String servletName = Constants.SERVLET_PATH + uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;
        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(Constants.WEB_ROOT);
            /**
             * 在一个 servlet 容器里边，一个类加载器可以找到 servlet 的地方被称为资源库 (repository）。
             *  getCanonicalPath会将文件路径解析为与操作系统相关的唯一的规范形式的字符串
             */
            String repository = (new URL("file", null, classPath.getCanonicalFile() + File.separator)).toString();
            /**
             * 第三个参数这样写不写null，使因为URL有多个构造函数，如果写null会引起歧义
             */
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 开始加载类
         */
        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        /**
         * 实例化servlet,并调用service方法
         */
        Servlet servlet = null;
        try {
            servlet = (Servlet) myClass.newInstance();
            /**
             * 避免暴露Request和Response自己的方法
             * 因为 Request可以向上转型为ServletRequest,ServletRequest也可以向下转型为Request,出于安全考虑
             */
            servlet.service(new RequestFacade(request), new ResponseFacade(response));
            response.finishResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
