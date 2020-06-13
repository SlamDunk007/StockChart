package com.guannan.stockchart;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.guannan.chartmodule.chart.KMasterChartView;
import com.guannan.chartmodule.chart.KSubChartView;
import com.guannan.chartmodule.chart.MarketFigureChart;
import com.guannan.chartmodule.data.ExtremeValue;
import com.guannan.chartmodule.data.KLineToDrawItem;
import com.guannan.chartmodule.data.SubChartData;
import com.guannan.chartmodule.helper.ChartDataSourceHelper;
import com.guannan.chartmodule.helper.TechParamType;
import com.guannan.chartmodule.inter.IChartDataCountListener;
import com.guannan.chartmodule.inter.IPressChangeListener;
import com.guannan.simulateddata.LocalUtils;
import com.guannan.simulateddata.parser.KLineParser;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements IChartDataCountListener<List<KLineToDrawItem>>, IPressChangeListener,
    RadioGroup.OnCheckedChangeListener {

  private ChartDataSourceHelper mHelper;
  private KMasterChartView mKLineChartView;
  private KSubChartView mVolumeView;
  private MarketFigureChart mMarketFigureChart;
  private ProgressBar mProgressBar;

  private int MAX_COLUMNS = 160;
  private int MIN_COLUMNS = 20;
  private KSubChartView mMacdView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initViews();

    // 行情图容器
    mMarketFigureChart = findViewById(R.id.chart_container);

    // 行情图主图（蜡烛线）
    mKLineChartView = new KMasterChartView(this);
    mMarketFigureChart.addChildChart(mKLineChartView, 200);

    // 行情图附图（成交量）
    mVolumeView = new KSubChartView(this);
    mMarketFigureChart.addChildChart(mVolumeView, 100);

    // MACD
    mMacdView = new KSubChartView(this);
    mMarketFigureChart.addChildChart(mMacdView, 100);

    // 容器的手势监听
    mMarketFigureChart.setPressChangeListener(this);
  }

  private void initialData(final String json) {
    mProgressBar.setVisibility(View.VISIBLE);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        initData(json);
      }
    }, 500);
  }

  private void initViews() {

    mProgressBar = findViewById(R.id.progress_circular);
    RadioGroup radioGroup = findViewById(R.id.rbtn_group);
    radioGroup.setOnCheckedChangeListener(this);
    radioGroup.check(R.id.rbtn_15);
  }

  @Override
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    switch (checkedId) {
      case R.id.rbtn_15:
        initialData("slw_k.json");
        break;
      case R.id.rbtn_1h:
        initialData("geli.json");
        break;
      case R.id.rbtn_4h:
        initialData("maotai.json");
        break;
      case R.id.rbtn_1d:
        initialData("pingan.json");
        break;
    }
  }

  /**
   * 解析行情图数据
   */
  public void initData(String json) {
    // 士兰微k线数据
    String kJson = LocalUtils.getFromAssets(this, json);

    KLineParser parser = new KLineParser(kJson);
    parser.parseKlineData();

    if (mHelper == null) {
      mHelper = new ChartDataSourceHelper(this);
    }
    mProgressBar.setVisibility(View.GONE);
    mHelper.initKDrawData(parser.klineList, mKLineChartView, mVolumeView, mMacdView);
  }

  /**
   * 对主图和附图进行数据填充
   */
  @Override
  public void onReady(List<KLineToDrawItem> data, ExtremeValue extremeValue,
      SubChartData subChartData) {
    mKLineChartView.initData(data, extremeValue,subChartData);
    mVolumeView.initData(data, extremeValue, TechParamType.VOLUME,subChartData);
    mMacdView.initData(data, extremeValue, TechParamType.MACD,subChartData);
  }

  /**
   * 主图的横向滑动
   */
  @Override
  public void onChartTranslate(MotionEvent me, float dX) {
    if (mHelper != null) {
      mHelper.initKMoveDrawData(dX, ChartDataSourceHelper.SourceType.MOVE);
    }
  }

  /**
   * 主图的手势fling
   */
  @Override
  public void onChartFling(float distanceX) {
    if (mHelper != null) {
      mHelper.initKMoveDrawData(distanceX, ChartDataSourceHelper.SourceType.FLING);
    }
  }

  @Override
  public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
    ChartDataSourceHelper.K_D_COLUMNS = (int) (ChartDataSourceHelper.K_D_COLUMNS / scaleX);
    ChartDataSourceHelper.K_D_COLUMNS =
        Math.max(MIN_COLUMNS, Math.min(MAX_COLUMNS, ChartDataSourceHelper.K_D_COLUMNS));
    if (mHelper != null) {
      mHelper.initKMoveDrawData(0, ChartDataSourceHelper.SourceType.SCALE);
    }
  }
}
