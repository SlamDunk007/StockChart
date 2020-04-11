package com.guannan.chartmodule.utils;

import java.math.BigDecimal;

/**
 * @author guannan
 * @date on 2020-04-11 16:09
 * @des 格式化数字
 */
public class NumberFormatUtils {

  public static float format(float d, int digits) {
    try {
      BigDecimal b = new BigDecimal(d);
      b = b.setScale(digits, BigDecimal.ROUND_HALF_UP);
      return b.floatValue();
    } catch (Exception e) {

    }
    return d;
  }

  public static float format(String d, int digits) {
    try {
      BigDecimal b = new BigDecimal(d);
      b = b.setScale(digits, BigDecimal.ROUND_HALF_UP);
      return b.floatValue();
    } catch (Exception e) {
      return 0.0f;
    }
  }
}
