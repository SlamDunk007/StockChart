package com.guannan.simulateddata.parser;

import android.text.TextUtils;
import com.guannan.simulateddata.entity.KLineItem;
import java.util.ArrayList;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * @author guannan
 * @date on 2020-03-14 16:32
 * @des k线数据解析
 */
public class KLineParser {

  /**
   * k线原始数据
   */
  private String mKlineJson;

  /**
   * k线列表
   */
  public ArrayList<KLineItem> klineList = new ArrayList<>();

  public KLineParser(String klineJson) {
    this.mKlineJson = klineJson;
  }

  /**
   * 解析K线数据
   */
  public void parseKlineData() {
    Object obj = JSONValue.parse(mKlineJson);
    if (obj instanceof JSONArray) {
      JSONArray jsonArray = (JSONArray) obj;
      if (jsonArray != null && jsonArray.size() > 0) {
        for (int i = 0; i < jsonArray.size(); i++) {
          JSONObject jsonObject = (JSONObject) jsonArray.get(i);
          KLineItem kLineItem = getKLineItem(jsonObject);
          klineList.add(kLineItem);
        }
      }
    }
  }

  /**
   * 获取每一个交易日数据
   */
  public KLineItem getKLineItem(JSONObject obj) {
    KLineItem kLineItem = new KLineItem();
    kLineItem.day = obj.getAsString("day");
    String open = obj.getAsString("open");
    if (!TextUtils.isEmpty(open)) {
      kLineItem.open = Float.parseFloat(open);
    }
    String high = obj.getAsString("high");
    if (!TextUtils.isEmpty(high)) {
      kLineItem.high = Float.parseFloat(high);
    }
    String low = obj.getAsString("low");
    if (!TextUtils.isEmpty(low)) {
      kLineItem.low = Float.parseFloat(low);
    }
    String close = obj.getAsString("close");
    if (!TextUtils.isEmpty(close)) {
      kLineItem.close = Float.parseFloat(close);
    }
    String volume = obj.getAsString("volume");
    if (!TextUtils.isEmpty(volume)) {
      kLineItem.volume = Long.parseLong(volume);
    }
    return kLineItem;
  }
}
