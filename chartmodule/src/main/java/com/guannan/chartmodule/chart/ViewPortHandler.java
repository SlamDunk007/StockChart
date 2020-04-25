package com.guannan.chartmodule.chart;

import android.graphics.RectF;

/**
 * @author guannan
 * @date on 2020-03-29 15:33
 * @des 行情图尺寸相关辅助方法
 */
public class ViewPortHandler {

  /**
   * 画布的宽高
   */
  private int mWidth, mHeight;

  /**
   * 绘制区域
   */
  public RectF mContentRect = new RectF();

  /**
   * 主图蜡烛线区域所占比例
   */
  private float ratio = 1;

  public ViewPortHandler() {

  }

  /**
   * 设置行情图的尺寸
   */
  public void setChartDimens(int width, int height) {

    float left = offLeft();
    float top = offTop();
    float right = offRight();
    float bottom = offBottom();

    this.mWidth = width;
    this.mHeight = height;
    restrainViewPort(left, top, right, bottom);
  }

  /**
   * 绘制日期区域所占比例
   */
  public void setContentRatio(float ratio) {
    this.ratio = ratio;
  }

  /**
   * 重置绘制区域的大小
   */
  public void restrainViewPort(float offLeft, float offTop, float offRight, float offBottom) {
    // 绘制的内容区域的大小
    mContentRect.set(offLeft, offTop, mWidth - offRight, mHeight * ratio - offBottom);
  }

  /**
   * 图表的宽度
   */
  public int getChartWidth() {
    return mWidth;
  }

  /**
   * 图表的高度
   */
  public int getChartHeight() {
    return mHeight;
  }

  /**
   * 左间距
   */
  public float offLeft() {
    return mContentRect.left;
  }

  /**
   * 上间距
   */
  public float offTop() {
    return mContentRect.top;
  }

  /**
   * 右间距
   */
  public float offRight() {
    return mWidth - mContentRect.right;
  }

  /**
   * 底部间距
   */
  public float offBottom() {
    return mContentRect.bottom;
  }
}
