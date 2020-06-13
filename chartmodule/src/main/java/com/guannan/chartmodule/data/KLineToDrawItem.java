package com.guannan.chartmodule.data;

import android.graphics.RectF;
import com.guannan.simulateddata.entity.KLineItem;
import java.io.Serializable;

/**
 * @author guannan
 * @date on 2020-03-29 16:25
 * @des 绘制在当前屏幕上的每一个交易日的数据
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

  /**
   * 当天交易日日期
   */
  public String date;

  /**
   * 成交量
   */
  public RectF volumeRect;

  /**
   * 日K数据
   */
  public KLineItem klineItem;

  public TechItem techItem;
}
