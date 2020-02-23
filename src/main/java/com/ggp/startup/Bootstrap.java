package com.ggp.startup;

import com.ggp.connector.http.HttpConnector;

/**
 * @author ggp
 * @Date 2020/2/23 15:26
 * @Description
 */
public class Bootstrap {
    public static void main(String[] args) {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.start();
    }
}
