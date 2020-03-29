package com.guannan.chartmodule.data;

import android.graphics.RectF;
import java.io.Serializable;

/**
 * @author guannan
 * @date on 2020-03-29 16:25
 * @des 绘制在屏幕上的K线元素
 */
public class KLineToDrawItem implements Serializable {

  /**
   * 蜡烛线
   */
  public RectF rect;

  /**
   * 上影线、下影线
   */
  public RectF shadowRect;

  /**
   * 当天涨跌： true:跌   false：涨
   */
  public boolean isFall;
}
