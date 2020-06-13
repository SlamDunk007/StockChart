package com.guannan.chartmodule.data;

/**
 * @author guannan
 * @date on 2020-06-10 14:38
 * @des
 */
public class TechItem {

  // MACD(利用收盘价的短期（常用为12日）指数移动平均线与长期（常用为26日）
  // 指数移动平均线之间的聚合与分离状况，对买进、卖出时机作出研判的技术指标)
  public float dif = .0f;
  public float dea = .0f;
  public float macd = .0f;

  // BOLL
  public float boll = .0f;
  public float upper = .0f;
  public float lower = .0f;
}
