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
    public static final String STATIC_SOURCE_PATH = "webroot";
    /**
     * 静态资源决对路径
     */
    public static final String WEB_ROOT = System.getProperty("user.dir")+File.separator + STATIC_SOURCE_PATH;
}
