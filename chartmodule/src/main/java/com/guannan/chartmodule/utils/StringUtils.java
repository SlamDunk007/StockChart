package com.guannan.chartmodule.utils;

/**
 * @author guannan
 * @date on 2020-06-10 19:54
 * @des 字符串操作相关
 */
public class StringUtils {

  /**
   * 获取拼接的参数 key：value 最高价：123.00
   */
  public static String getAppendStr(String arg1, float arg2) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(arg1);
    stringBuilder.append(arg2);
    return stringBuilder.toString();
  }

  /**
   * 获取拼接的参数 key：value 最高价：123.00
   */
  public static String getAppendStr(String arg1, String arg2) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(arg1);
    stringBuilder.append(arg2);
    return stringBuilder.toString();
  }
}
