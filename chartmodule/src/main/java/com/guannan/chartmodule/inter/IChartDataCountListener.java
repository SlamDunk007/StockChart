package com.guannan.chartmodule.inter;

import com.guannan.chartmodule.data.ExtremeValue;
import com.guannan.chartmodule.data.SubChartData;

/**
 * @author guannan
 * @date on 2020-04-11 13:57
 * @des 行情图数据准备完毕回调
 */
public interface IChartDataCountListener<T> {

  void onReady(T data, ExtremeValue extremeValue, SubChartData subChartData);
}
