package com.guannan.chartmodule.utils;

import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;

/**
 * @author guannan
 * @date on 2020-04-11 14:18
 * @des 画笔工具类
 */
public class PaintUtils {

  private static int TEXT_SIZE_SP;

  /**
   * 行情图外部分隔线
   */
  public static Paint GRID_DIVIDER = new Paint();
  public final static int COLOR_GRID_DIVIDER = 0xffe5e6f2;

  /**
   * 行情图内部分隔线
   */
  public static Paint GRID_INNER_DIVIDER = new Paint();

  /**
   * 表格外文字
   */
  public static Paint TEXT_PAINT = new Paint();
  public final static int C_FRAME_FONT = 0xff747985;

  /**
   * 选中十字线
   */
  public static Paint FOCUS_LINE_PAINT = new Paint();
  public final static int C_FOCUS_BG_BLACK = 0xff000000;

  static {

    GRID_DIVIDER.setColor(COLOR_GRID_DIVIDER);
    GRID_DIVIDER.setStyle(Paint.Style.STROKE);
    GRID_DIVIDER.setStrokeWidth(2);
    GRID_DIVIDER.setAntiAlias(true);

    GRID_INNER_DIVIDER.setColor(COLOR_GRID_DIVIDER);
    GRID_INNER_DIVIDER.setStyle(Paint.Style.STROKE);
    GRID_INNER_DIVIDER.setStrokeWidth(1);
    GRID_INNER_DIVIDER.setAntiAlias(true);

    TEXT_PAINT.setAntiAlias(true);
    TEXT_PAINT.setColor(C_FRAME_FONT);

    FOCUS_LINE_PAINT.setColor(C_FOCUS_BG_BLACK);
    FOCUS_LINE_PAINT.setStrokeWidth(2);
  }

  public static void init(Context context) {
    TEXT_SIZE_SP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8,
        context.getResources().getDisplayMetrics());
    TEXT_PAINT.setTextSize(TEXT_SIZE_SP);
  }
}
