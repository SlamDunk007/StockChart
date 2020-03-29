package com.guannan.simulateddata;

import android.content.Context;
import com.squareup.duktape.Duktape;
import com.squareup.duktape.DuktapeException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guannan
 * @date on 2020-02-24 15:50
 */
public class JsEngineForStock {

  public interface JsConnectApi {
    String parseToJs(String src);
  }

  /**
   *
   */
  public static JsBuilder create(Context context) {
    Duktape duktape = null;
    JsBuilder jsBuilder = new JsBuilder(null, null);
    try {
      duktape = Duktape.create();
      StringBuilder builder = new StringBuilder();
      String
          jsStr =
          LocalUtils.getFromAssets(context, "sklcd-raw.js");
      builder.append(jsStr);
      builder.append(
          "var unzipMSCI = {parseToJs: function(src) { return JSON.stringify(S_KLC_D(src)); }};");
      duktape.evaluate(builder.toString());
      jsBuilder.setDuktape(duktape);
      jsBuilder.setJsConnectApi(duktape.get("unzipMSCI", JsConnectApi.class));
    } catch (DuktapeException e) {
      if (duktape != null) {
        duktape.close();
        duktape = null;
      }
    }
    return jsBuilder;
  }

  public static class JsBuilder {
    Duktape duktape;
    JsConnectApi jsConnectApi;

    public JsBuilder(Duktape duktape, JsConnectApi jsConnectApi) {
      this.duktape = duktape;
      this.jsConnectApi = jsConnectApi;
    }

    public void setDuktape(Duktape duktape) {
      this.duktape = duktape;
    }

    public void setJsConnectApi(JsConnectApi jsConnectApi) {
      this.jsConnectApi = jsConnectApi;
    }

    /**
     * 解压缩MSCI的K线数据
     */
    public String parseKLine(String json) {
      if (jsConnectApi != null && duktape != null) {
        String s = jsConnectApi.parseToJs(json);
        return s;
      }
      return null;
    }

    /**
     * 解析期货分时数据
     */
    public String parseFuture(String json) {
      if (jsConnectApi != null && duktape != null) {
        String s = jsConnectApi.parseToJs(json);
        return s;
      }
      return null;
    }

    public List<String> parse(String[] source) {
      if (source == null || source.length <= 0) {
        return null;
      }
      List<String> resultList = null;
      try {
        if (jsConnectApi != null && duktape != null) {
          resultList = new ArrayList<>();
          for (String src : source) {
            String result = jsConnectApi.parseToJs(src);
            resultList.add(result);
          }
        }
      } catch (DuktapeException e) {
      } finally {
        if (jsConnectApi != null) {
          jsConnectApi = null;
        }
        if (duktape != null) {
          duktape.close();
          duktape = null;
        }
      }
      return resultList;
    }
  }
}
