package com.ggp.servlet;

import org.apache.log4j.Logger;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ggp
 * @Date 2020/1/30 17:22
 * @Description
 * 一个全功能的servlet容器会为servlet的每个HTTP请求做下面一些工作：
 * * 当第一次调用 servlet 的时候，加载该 servlet 类并调用 servlet 的 init 方法(仅仅一 次)。
 * * 对每次请求，构造一个 javax.servlet.ServletRequest 实例和一个 javax.servlet.ServletResponse 实例。
 * * 调用 servlet 的 service 方法，同时传递 ServletRequest 和 ServletResponse 对象。
 * * 当 servlet 类被关闭的时候，调用 servlet 的 destroy 方法并卸载 servlet 类。
 */
public class PrimitiveServlet implements Servlet {
    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * init 方法必须在 servlet 可以接受任何请求之前成功运行完毕。
     * 一个 servlet 程序员可以通过覆盖这个方法来写那些仅仅只要运行一次的初始化代码，例如加载 数据库驱动，值初始化等等。
     * 在其他情况下，这个方法通常是留空的
     *
     * @param servletConfig
     * @throws ServletException
     */
    public void init(ServletConfig servletConfig) throws ServletException {
        logger.debug("init");
    }

    /**
     * servlet容器在收到请求后会调用service方法。
     * ServletRequest 对象包括客户端的 HTTP 请求信息，
     * 而 ServletResponse 对象封装 servlet 的响应。
     * 在 servlet 的生命周期中，service 方法将会给调用多次。
     *
     * @param servletRequest
     * @param servletResponse
     * @throws ServletException
     * @throws IOException
     */
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        logger.debug("service");
        PrintWriter out = servletResponse.getWriter();
        out.println("hello servlet");
    }

    /**
     * 当从服务中移除一个 servlet 实例的时候，servlet 容器调用 destroy 方法。
     * 这通常发生在 servlet 容器正在被关闭或者 servlet 容器需要一些空闲内存的时候。
     * 仅仅在所有 servlet 线程 的 service 方法已经退出或者超时淘汰的时候，这个方法才被调用。
     * 在 servlet 容器已经调用完 destroy 方法之后，在同一个 servlet 里边将不会再调用 service 方法。
     * destroy 方法提供了一 个机会来清理任何已经被占用的资源，例如内存，文件句柄和线程，并确保任何持久化状态和 servlet 的内存当前状态是同步的。
     */
    public void destroy() {
        logger.debug("destroy");
    }

    public ServletConfig getServletConfig() {
        return null;
    }

    public String getServletInfo() {
        return null;
    }


}
