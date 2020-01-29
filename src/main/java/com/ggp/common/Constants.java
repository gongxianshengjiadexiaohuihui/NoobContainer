package com.ggp.common;

import java.io.File;

/**
 * @author: ggp
 * @Date: 2020/1/20 15:56
 * @Description:
 */
public class Constants {
    /**
     * 静态资源相对路径
     */
    public static final String STATIC_SOURCE_PATH = "WEB_ROOT";
    /**
     * 静态资源决对路径
     */
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + STATIC_SOURCE_PATH;
    /**
     * 关闭命令
     */
    public static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
    /**
     * 默认ip
     */
    public static final String DEFAULT_IP = "127.0.0.1";
    /**
     * 默认端口
     */
    public static final Integer DEFAULT_PORT = 8080;

}