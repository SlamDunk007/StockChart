package com.guannan.chartmodule.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author guannan
 * @date on 2020-03-14 17:24
 * @des 屏幕尺寸相关工具类
 */
public class DisplayUtils {

  private static DisplayMetrics dMetrics = null;
  private static int DisplayMaxWidth = 0;
  private static int DisplayWidth = 0;

  /**
   * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
   */
  public static int dip2px(Context context, float dpValue) {
    if (context == null) return 0;
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /**
   * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
   */
  public static int px2dip(Context context, float pxValue) {
    if (context == null) return 0;
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  public static DisplayMetrics getDisplayMetrics(Context activity) {
    if (dMetrics == null && activity != null) {
      dMetrics = activity.getApplicationContext().getResources().getDisplayMetrics();
    }
    return dMetrics;
  }

  /**
   * 获取屏幕宽度
   */
  public static int getDisPlayMaxWidth(Activity activity) {
    if (DisplayMaxWidth <= 0) {
      DisplayMetrics dMetrics = getDisplayMetrics(activity);
      if (dMetrics != null) {
        if (dMetrics.widthPixels < dMetrics.heightPixels) {
          DisplayMaxWidth = dMetrics.heightPixels;
        } else {
          DisplayMaxWidth = dMetrics.widthPixels;
        }
      }
    }

    if (DisplayMaxWidth <= 0) {
      return 800;
    } else {
      return DisplayMaxWidth;
    }
  }

  public static int getDisPlayWidth(Activity activity) {
    if (DisplayWidth <= 0) {
      DisplayMetrics dMetrics = getDisplayMetrics(activity);
      if (dMetrics != null) {
        if (dMetrics.widthPixels < dMetrics.heightPixels) {
          DisplayWidth = dMetrics.widthPixels;
        } else {
          DisplayWidth = dMetrics.heightPixels;
        }
      }
    }

    if (DisplayWidth <= 0) {
      return 480;
    } else {
      return DisplayWidth;
    }
  }
}
