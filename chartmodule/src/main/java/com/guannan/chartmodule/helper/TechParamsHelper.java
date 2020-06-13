package com.guannan.chartmodule.helper;

import com.guannan.chartmodule.data.TechItem;
import com.guannan.chartmodule.utils.DataUtils;
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
   */
  public Limit getLimitValue(TechItem techItem, TechParamType techParamType) {
    Limit limit = new Limit();
    if (techParamType == TechParamType.MACD) {
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
    } else if (techParamType == TechParamType.BOLL) {
      limit.max = techItem.upper;
      limit.min = techItem.lower;
    }
    return limit;
  }

  /**
   * 计算技术指标
   */
  public void caculateTechParams(List<KLineItem> list, TechParamType techParamType) {
    // 附图MACD
    if (techParamType == TechParamType.MACD) {
      linkDataMACD(list);
    } else if (techParamType == TechParamType.BOLL) {
      linkDataBOLL(list);
    }
  }

  /**
   * BOLL
   */
  private void linkDataBOLL(List<KLineItem> list) {
    int len = list.size();
    KLineItem itemK;
    TechItem itemT;

    final int _EN = 20;
    float sum = .0f, std2 = .0f, stdsum = .0f;
    float tmpArr[] = new float[len];

    for (int i = 0; i < len; i++) {
      itemK = list.get(i);
      itemT = getTechItem(i);

      sum += itemK.close;
      if (i >= _EN - 1) {
        itemT.boll = sum / _EN;
        sum -= list.get(i - _EN + 1).close;
      } else {
        itemT.boll = sum / (i + 1);
      }
      // 差值的平方
      tmpArr[i] = (float) Math.pow((itemK.close - itemT.boll), 2);
      stdsum += tmpArr[i];
      if (i > _EN - 1) {
        stdsum -= tmpArr[i - _EN];
        if (stdsum < .0) {
          stdsum = Math.abs(stdsum);
        }
      }
      std2 = (float) (2 * Math.sqrt(stdsum / Math.min(i + 1, _EN)));
      if (std2 < DataUtils.EPSILON) {
        std2 = itemT.boll * 0.1f;
      }
      itemT.upper = itemT.boll + std2;
      itemT.lower = itemT.boll - std2;
    }
  }

  /**
   * MACD
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
