package com.guannan.chartmodule.inter;

/**
 * @author guannan
 * @date on 2020-04-11 13:57
 * @des 行情图数据准备完毕回调
 */
public interface OnChartDataCountListener<T> {

  void onReady(T data, float maxValue, float minValue);
}
