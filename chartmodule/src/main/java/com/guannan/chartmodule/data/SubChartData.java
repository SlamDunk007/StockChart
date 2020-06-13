package com.guannan.chartmodule.data;

import android.graphics.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guannan
 * @date on 2020-06-12 15:48
 * @des 附图数据
 */
public class SubChartData {

  /**
   * MACD
   */
  public int MACDLINES = 2;

  /**
   * macd线
   */
  public Path[] macdPaths = new Path[2];

  public List<LineRectItem> macdRects = new ArrayList<>();

  public Path[] bollPaths = new Path[3];

  public SubChartData() {
    for (int i = 0; i < MACDLINES; i++) {
      macdPaths[i] = new Path();
    }
    for (int j = 0; j < 3; j++) {
      bollPaths[j] = new Path();
    }
  }

  public void reset() {
    if (macdPaths != null) {
      for (Path macdPath : macdPaths) {
        if (macdPath != null) {
          macdPath.reset();
        }
      }
    }
    if (bollPaths != null) {
      for (Path bollPath : bollPaths) {
        bollPath.reset();
      }
    }
    if (macdRects != null) {
      macdRects.clear();
    }
  }
}
