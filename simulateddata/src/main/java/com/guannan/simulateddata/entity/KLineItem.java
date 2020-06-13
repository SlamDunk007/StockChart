package com.guannan.simulateddata.entity;

/**
 * @author guannan
 * @date on 2020-03-14 16:34
 * @des k线数据实体
 */
public class KLineItem {

  /**
   * 交易日日期
   */
  public String day;

  /**
   * 开盘价
   */
  public float open;

  /**
   * 最高价
   */
  public float high;

  /**
   * 最低价
   */
  public float low;

  /**
   * 收盘价
   */
  public float close;

  /**
   * 成交量
   */
  public long volume;

  /**
   * 昨日收盘价
   */
  public float preClose;

}
