package com.guannan.chartmodule.helper;

import com.guannan.chartmodule.data.TechItem;
import com.guannan.simulateddata.entity.KLineItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guannan
 * @date on 2020-06-10 16:12
 * @des 技术指标相关计算
 */
public class TechParamsHelper {

  public class Limit {
    public float max = .0f;
    public float min = .0f;
  }

  public List<TechItem> listParams = new ArrayList<>();

  private TechItem getTechItem(int index) {
    int size = listParams.size();
    TechItem itemT = null;
    if (index < size) {
      itemT = listParams.get(index);
    } else {
      while (index >= size) {
        itemT = new TechItem();
        listParams.add(itemT);
        size++;
      }
    }
    return itemT;
  }

  /**
   * 获取技术指标的极值
   * @param techItem
   * @return
   */
  public Limit getLimitValue(TechItem techItem) {
    Limit limit = new Limit();
    limit.max = techItem.dea;
    limit.min = techItem.dif;
    if (techItem.dea < techItem.dif) {
      limit.max = techItem.dif;
      limit.min = techItem.dea;
    }
    if (limit.max < techItem.macd) {
      limit.max = techItem.macd;
    }
    if (limit.min > techItem.macd) {
      limit.min = techItem.macd;
    }
    return limit;
  }

  /**
   * 计算技术指标
   * @param list
   * @param techParamType
   */
  public void caculateTechParams(List<KLineItem> list, TechParamType techParamType) {
    // 附图MACD
    if (techParamType == TechParamType.MACD) {
      linkDataMACD(list);
    }
  }

  /**
   * 附图MACD
   */
  public void linkDataMACD(List<KLineItem> list) {
    int len = list.size();
    KLineItem itemK = list.get(0);
    TechItem techItem = null;
    TechItem preTechItem = getTechItem(0);

    final int _E1 = 12, _E2 = 26, _EDEA = 9;
    float ema1 = itemK.close, ema2 = itemK.close;

    for (int i = 1; i < len; i++) {
      itemK = list.get(i);
      techItem = getTechItem(i);

      ema1 = _calcEMA(ema1, itemK.close, _E1);
      ema2 = _calcEMA(ema2, itemK.close, _E2);
      techItem.dif = ema1 - ema2;
      techItem.dea = _calcEMA(preTechItem.dea, techItem.dif, _EDEA);
      techItem.macd = (techItem.dif - techItem.dea) * 2;

      preTechItem = techItem;
    }
  }

  public static float _calcEMA(float ema0_, float close_, int cycle_) {
    int cycleDiv = cycle_ + 1;
    return (cycle_ - 1) * ema0_ / cycleDiv + close_ * 2 / cycleDiv;
  }
}
