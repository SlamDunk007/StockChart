package com.guannan.chartmodule.utils;

import java.math.BigDecimal;

/**
 * @author guannan
 * @date on 2020-04-11 16:09
 * @des 格式化数字
 */
public class NumFormatUtils {

  public static float formatFloat(float d, int digits) {
    try {
      BigDecimal b = new BigDecimal(d);
      b = b.setScale(digits, BigDecimal.ROUND_HALF_UP);
      return b.floatValue();
    } catch (Exception e) {

    }
    return d;
  }

  /**
   * 支持负数的格式化(带单位)
   */
  public static String formatBigFloatAll(float d, int digits) {
    if (d < DataUtils.EPSILONNGT) {
      if (d > -1E4) {
        return formatFloat(d, 0) + "";
      } else if (d > -1E8) {
        return formatFloat((float) (d / 1E4), digits) + "万";
      } else {
        return formatFloat((float) (d / 1E8), digits) + "亿";
      }
    } else {
      if (d < 1E4) {
        return formatFloat(d, 0) + "";
      } else if (d < 1E8) {
        return formatFloat((float) (d / 1E4), digits) + "万";
      } else if (d < 1E12) {
        return formatFloat((float) (d / 1E8), digits) + "亿";
      } else {
        if (d < 1E13) {
          return formatFloat((float) (d / 1E8), digits) + "亿";
        } else {
          return formatFloat((float) (d / 1E12), digits) + "万亿";
        }
      }
    }
  }

  public static String formatFloat(float d, int digits, boolean isAddPercentSign,
      boolean isAddPlus, String defaultRet, String unit, boolean isZeroReturnDefault) {

    if (isZeroReturnDefault && DataUtils.isZero(d)) {
      return defaultRet;
    }
    try {
      BigDecimal b = new BigDecimal(Float.toString(d));
      StringBuilder sb = new StringBuilder(b.setScale(digits, BigDecimal.ROUND_HALF_UP).toString());
      if (isAddPercentSign) {
        sb.append('%');
      } else {
        sb.append(unit);
      }

      if (d > 0 && isAddPlus) {
        sb.insert(0, '+');
      }
      return sb.toString();
    } catch (NumberFormatException e) {
      return defaultRet;
    }
  }
}
