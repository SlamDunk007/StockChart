package com.guannan.chartmodule.utils;

/**
 * @author guannan
 * @date on 2020-04-11 14:11
 * @des 日期相关处理
 */
public class DateUtils {

  public static int getMonth(String date) {
    if (date != null && date.length() >= 10) {
      // "yyyy-MM-dd"
      StringBuffer sb = new StringBuffer(date);
      int month = Short.parseShort(sb.substring(5, 7));
      return month;
    }
    return -1;
  }

  public static String getYMD(String date) {
    if (date != null && date.length() >= 10) {
      return date.substring(0, 10);
    }
    return date;
  }
}
