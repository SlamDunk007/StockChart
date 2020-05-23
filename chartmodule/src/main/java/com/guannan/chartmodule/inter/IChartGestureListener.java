package com.guannan.chartmodule.inter;

import android.view.MotionEvent;
import com.guannan.chartmodule.helper.ChartTouchHelper;

/**
 * @author guannan
 * @date on 2020-03-14 11:28
 * @des 手势识别监听
 */
public interface IChartGestureListener {

  /**
   * 当手势开始的时候触发，一般是(ACTION_DOWN)
   *
   * @param me 事件
   * @param lastPerformedGesture 最后的手势类型
   */
  void onChartGestureStart(MotionEvent me, ChartTouchHelper.ChartGesture lastPerformedGesture);

  /**
   * 当手势结束的时候，一般是(ACTION_UP, ACTION_CANCEL)
   *
   * @param me 事件
   * @param lastPerformedGesture 最后的手势类型
   */
  void onChartGestureEnd(MotionEvent me, ChartTouchHelper.ChartGesture lastPerformedGesture);

  /**
   * 当在图表上执行长按的时候调用
   */
  void onChartLongPressed(MotionEvent me);

  /**
   * 当在图表上执行双击的时候调用
   */
  void onChartDoubleTapped(MotionEvent me);

  /**
   * 当在图表上执行单击的时候调用
   */
  void onChartSingleTapped(MotionEvent me);

  /**
   * 当在图表上执行fling的时候调用
   */
  void onChartFling(float distanceX);

  /**
   * 当在图表上执行缩放的时候调用
   *
   * @param scaleX x轴缩放系数
   * @param scaleY y轴缩放系数
   */
  void onChartScale(MotionEvent me, float scaleX, float scaleY);

  /**
   * 当在图标执行move，drag的时候调用
   *
   * @param dX x轴移动距离
   */
  void onChartTranslate(MotionEvent me, float dX);
}
