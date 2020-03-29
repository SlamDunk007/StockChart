package com.guannan.chartmodule.help;

import android.graphics.RectF;
import com.guannan.chartmodule.chart.ViewPortHandler;
import com.guannan.chartmodule.data.KLineToDrawItem;
import com.guannan.simulateddata.entity.KLineItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guannan
 * @date on 2020-03-14 17:09
 * @des 行情图数据辅助类
 */
public class ChartDataSourceHelper {

  /**
   * K线相关
   */
  /**
   * 一屏默认展示的蜡烛线数量
   */
  public static final int K_D_COLUMNS = 60;

  /**
   * 蜡烛线的最大值（最高价格）
   */
  public float maxPrice = Float.MIN_VALUE;

  /**
   * 蜡烛线的最小值
   */
  public float minPrice = Float.MAX_VALUE;

  /**
   * 行情图当前屏开始的位置
   */
  public int startIndex;

  /**
   * 行情图当前屏结束位置
   */
  public int endIndex;

  /**
   * k线的绘制数据
   */
  public List<KLineToDrawItem> kLineItems;

  /**
   * 尺寸辅助类
   */
  private ViewPortHandler mViewPortHandler;

  private List<KLineItem> mKList;

  /**
   * 初始化行情图初始数据
   */
  public void initKDrawData(List<KLineItem> klineList, ViewPortHandler viewPortHandler) {
    this.mKList = klineList;
    this.mViewPortHandler = viewPortHandler;
    // K线首次当前屏初始位置
    startIndex = Math.max(0, klineList.size() - K_D_COLUMNS);
    // k线首次当前屏结束位置
    endIndex = klineList.size() - 1;
    initKMoveDrawData(0);
  }

  /**
   * 根据移动偏移量计算行情图当前屏数据
   *
   * @param distance 手指横向移动距离
   */
  public void initKMoveDrawData(float distance) {

    // 重置最大最小值
    maxPrice = Float.MIN_VALUE;
    minPrice = Float.MAX_VALUE;
    if (kLineItems == null) {
      kLineItems = new ArrayList<>();
    } else {
      kLineItems.clear();
    }

    // 根据偏移距离计算偏移几天
    int offCount = (int) ((distance * ChartDataSourceHelper.K_D_COLUMNS)
        / mViewPortHandler.mContentRect.width());
    // 计算移动后的开始和结束位置
    startIndex = startIndex - offCount;
    endIndex = startIndex + K_D_COLUMNS;

    if (endIndex > mKList.size()) {
      startIndex = mKList.size() - K_D_COLUMNS;
      endIndex = startIndex + K_D_COLUMNS;
    }
    for (int i = startIndex; i < endIndex; i++) {
      KLineItem kLineItem = mKList.get(i);
      if (kLineItem != null) {
        if (kLineItem.high > maxPrice) {
          maxPrice = kLineItem.high;
        }
        if (kLineItem.low < minPrice) {
          minPrice = kLineItem.low;
        }
      }
    }

    // 最大值最小值差值
    float diffPrice = maxPrice - minPrice;

    // 计算当前屏幕每一个蜡烛线的位置和涨跌情况
    for (int i = startIndex, k = 0; i < startIndex + K_D_COLUMNS; i++, k++) {
      KLineItem kLineItem = mKList.get(i);

      // 开盘价
      float open = kLineItem.open;
      // 最低价
      float close = kLineItem.close;
      // 最高价
      float high = kLineItem.high;
      // 最低价
      float low = kLineItem.low;

      RectF contentRect = mViewPortHandler.mContentRect;
      KLineToDrawItem drawItem = new KLineToDrawItem();
      // 计算蜡烛线
      float scaleY_open = (maxPrice - open) / diffPrice;
      float scaleY_low = (maxPrice - close) / diffPrice;
      RectF candleRect = getRect(contentRect, k, scaleY_open, scaleY_low);
      drawItem.rect = candleRect;
      // 计算上影线，下影线
      float scale_HL_T = (maxPrice - high) / diffPrice;
      float scale_HL_B = (maxPrice - low) / diffPrice;
      RectF shadowRect = getLine(contentRect, k, scale_HL_T, scale_HL_B);
      drawItem.shadowRect = shadowRect;
      // 计算红涨绿跌，暂时这么计算（其实红涨绿跌是根据当前开盘价和前一天的收盘价做对比）
      if (i - 1 >= 0) {
        KLineItem preItem = mKList.get(i - 1);
        if (kLineItem.open > preItem.close) {
          drawItem.isFall = false;
        } else {
          drawItem.isFall = true;
        }
      }
      kLineItems.add(drawItem);
    }
  }

  /**
   * 蜡烛图的区域，间隙0.125，蜡烛图宽度占0.75.
   */
  public RectF getRect(RectF parent, int col, float scaleTop,
      float scaleBottom) {
    RectF rect = new RectF();
    rect.left = parent.left + (int) (parent.width() * (col + 0.125)
        / K_D_COLUMNS);
    rect.right = parent.left + (int) (parent.width() * (col + 0.875)
        / K_D_COLUMNS);
    if (rect.right == rect.left) {
      rect.right++;
    }
    rect.top = parent.top + (int) (parent.height() * scaleTop);
    rect.bottom = parent.top + (int) (parent.height() * scaleBottom);
    if (rect.top == rect.bottom) {
      rect.bottom += 1;
    }
    return rect;
  }

  /**
   * 蜡烛线上影线，和下影线的宽度和高度
   */
  public RectF getLine(RectF parent, int col, float scaleTop,
      float scaleBottom) {
    RectF rect = new RectF();
    rect.left = (int) (parent.left + (int) (parent.width() * (col + 0.5)
        / K_D_COLUMNS) - 1.5);
    rect.right = (int) (parent.left + (int) (parent.width() * (col + 0.5)
        / K_D_COLUMNS) + 1.5);

    rect.top = parent.top + (int) (parent.height() * scaleTop);
    rect.bottom = parent.top + (int) (parent.height() * scaleBottom);
    return rect;
  }
}
