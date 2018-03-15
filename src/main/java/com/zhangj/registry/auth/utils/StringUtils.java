package com.zhangj.registry.auth.utils;

/**
 * @author zhangjun
 * @description
 * @date 2018/3/15
 */
public class StringUtils {
    public static String trimRight(String s, char symbol) {
        StringBuffer temp = new StringBuffer(s);
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == symbol) {
                temp.deleteCharAt(i);
            } else {
                break;
            }
        }
        return temp.toString();
    }
}
