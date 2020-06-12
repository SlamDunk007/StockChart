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

  /**
   * 长按弹框背景颜色
   */
  public static Paint POP_DIALOG_PAINT = new Paint();
  public final static int C_POP_DIALOG_BG = 0x99091622;

  /**
   * 表格外文字
   */
  public static Paint TEXT_POP_PAINT = new Paint();
  public final static int C_TEXT_POP = 0xffffffff;

  /**
   * 红
   */
  public static Paint TEXT_RED_PAINT = new Paint();
  public final static int C_TEXT_RED = 0xfffd4331;

  /**
   * 绿
   */
  public static Paint TEXT_GREEN_PAINT = new Paint();
  public final static int C_TEXT_GREEN = 0xff05aa3b;

  public static Paint MA_LINE_DEA_PAINT = new Paint();
  public final static int C_LINE_DEA = 0XFFFFA100;

  public static Paint MA_LINE_DIF_PAINT = new Paint();
  public final static int C_LINE_DIF = 0XFF4E8BEE;

  public static Paint MA_LINE_MACD_PAINT = new Paint();
  public final static int C_LINE_MACD = 0XFFFF3FBE;

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

    POP_DIALOG_PAINT.setColor(C_POP_DIALOG_BG);
    POP_DIALOG_PAINT.setAntiAlias(true);
    POP_DIALOG_PAINT.setStyle(Paint.Style.FILL);

    TEXT_POP_PAINT.setColor(C_TEXT_POP);
    TEXT_POP_PAINT.setAntiAlias(true);

    TEXT_RED_PAINT.setColor(C_TEXT_RED);
    TEXT_RED_PAINT.setAntiAlias(true);

    TEXT_GREEN_PAINT.setColor(C_TEXT_GREEN);
    TEXT_GREEN_PAINT.setAntiAlias(true);

    MA_LINE_DEA_PAINT.setStyle(Paint.Style.STROKE);
    MA_LINE_DEA_PAINT.setStrokeWidth(2);
    MA_LINE_DEA_PAINT.setColor(C_LINE_DEA);
    MA_LINE_DEA_PAINT.setStrokeCap(Paint.Cap.ROUND);
    MA_LINE_DEA_PAINT.setAntiAlias(true);

    MA_LINE_DIF_PAINT.setStyle(Paint.Style.STROKE);
    MA_LINE_DIF_PAINT.setStrokeWidth(2);
    MA_LINE_DIF_PAINT.setColor(C_LINE_DIF);
    MA_LINE_DIF_PAINT.setStrokeCap(Paint.Cap.ROUND);
    MA_LINE_DIF_PAINT.setAntiAlias(true);

    MA_LINE_MACD_PAINT.setStyle(Paint.Style.STROKE);
    MA_LINE_MACD_PAINT.setStrokeWidth(2);
    MA_LINE_MACD_PAINT.setColor(C_LINE_MACD);
    MA_LINE_MACD_PAINT.setStrokeCap(Paint.Cap.ROUND);
    MA_LINE_MACD_PAINT.setAntiAlias(true);
  }

  public static void init(Context context) {
    TEXT_SIZE_SP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8,
        context.getResources().getDisplayMetrics());
    TEXT_PAINT.setTextSize(TEXT_SIZE_SP);
    float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
        context.getResources().getDisplayMetrics());
    TEXT_POP_PAINT.setTextSize(textSize);
    TEXT_RED_PAINT.setTextSize(textSize);
    TEXT_GREEN_PAINT.setTextSize(textSize);
  }
}
