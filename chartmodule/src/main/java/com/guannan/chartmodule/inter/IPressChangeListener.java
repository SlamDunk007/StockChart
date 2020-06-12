package com.guannan.chartmodule.inter;

import android.view.MotionEvent;

/**
 * @author guannan
 * @date on 2020-04-21 20:09
 * @des TODO
 */
public interface IPressChangeListener {

  void onChartTranslate(MotionEvent me, float dX);

  void onChartFling(float distanceX);

  void onChartScale(MotionEvent me, float scaleX, float scaleY);
}
