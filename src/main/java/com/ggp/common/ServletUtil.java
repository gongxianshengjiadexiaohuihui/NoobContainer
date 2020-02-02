package com.ggp.common;

import javax.servlet.http.Cookie;
import java.util.ArrayList;

/**
 * @author ggp
 * @Date 2020/2/2 19:30
 * @Description
 */
public class ServletUtil {
    /**
     * 解析cookie
     * @param header
     * @return
     */
    public static Cookie[] parseCookieHeader(String header){
         if(null == header||header.length() ==0){
             return (new Cookie[0]);
         }
         ArrayList cookies = new ArrayList();
         while(header.length()>0){
             int semicolon = header.indexOf(';');
             if(semicolon < 0){
                 semicolon = header.length();
             }
             /**
              * 循环终止条件
              */
             if(semicolon == 0){
                 break;
             }
             /**
              * 截取单个cookie属性
              */
             String token = header.substring(0,semicolon);
             /**
              * 方便解析下一个cookie属性
              */
             if(semicolon<header.length()){
                 header = header.substring(semicolon+1);
             }else{
                 header = "";
             }
             /**
              * 解析单个cookie属性
              */
             int equals = token.indexOf('=');
             if(equals>0){
                 String name = token.substring(0,equals).trim();
                 String value = token.substring(equals+1).trim();
                 cookies.add(new Cookie(name,value));
             }
         }
         //todo
         return (Cookie[]) cookies.toArray(new Cookie[cookies.size()]);
    }
}
