package com.guannan.chartmodule.utils;

import android.util.Log;

/**
 * @author guannan
 * @date on 2020-03-14 10:43
 * @des 日志工具类
 */
public class LogUtils {

  private static final String TAG = "guannan_log";

  public static void d(String msg) {
    Log.d(TAG, "d: " + msg);
  }
}
