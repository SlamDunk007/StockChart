package com.guannan.stockchart;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.guannan.chartmodule.chart.KMasterChartView;
import com.guannan.chartmodule.chart.KSubChartView;
import com.guannan.chartmodule.chart.MarketFigureChart;
import com.guannan.chartmodule.data.ExtremeValue;
import com.guannan.chartmodule.data.KLineToDrawItem;
import com.guannan.chartmodule.helper.ChartDataSourceHelper;
import com.guannan.chartmodule.inter.IChartDataCountListener;
import com.guannan.chartmodule.inter.IPressChangeListener;
import com.guannan.simulateddata.SimulatedManager;
import com.guannan.simulateddata.parser.KLineParser;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements IChartDataCountListener<List<KLineToDrawItem>>, IPressChangeListener {

  private ChartDataSourceHelper mHelper;
  private KMasterChartView mKLineChartView;
  private KSubChartView mVolumeView;
  private MarketFigureChart mMarketFigureChart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 行情图容器
    mMarketFigureChart = findViewById(R.id.chart_container);

    // 行情图主图（蜡烛线）
    mKLineChartView = new KMasterChartView(this);
    mMarketFigureChart.addChildChart(mKLineChartView, 200);

    // 行情图附图（成交量）
    mVolumeView = new KSubChartView(this);
    mMarketFigureChart.addChildChart(mVolumeView, 100);

    // 容器的手势监听
    mMarketFigureChart.setPressChangeListener(this);
  }

  /**
   * 解析行情图数据
   */
  public void parse(View view) {
    String kLineData = SimulatedManager.getKLineData(this, SimulatedManager.KLineTotalJson);
    KLineParser parser = new KLineParser(kLineData);
    parser.parseKlineData();

    if (mHelper == null) {
      mHelper = new ChartDataSourceHelper(this);
    }
    mHelper.initKDrawData(parser.klineList, mKLineChartView, mVolumeView);
  }

  /**
   * 对主图和附图进行数据填充
   */
  @Override
  public void onReady(List<KLineToDrawItem> data, ExtremeValue extremeValue) {
    mKLineChartView.initData(data, extremeValue);
    mVolumeView.initData(data, extremeValue);
  }

  /**
   * 主图的横向滑动
   */
  @Override
  public void onChartTranslate(MotionEvent me, float dX) {
    if (mHelper != null) {
      mHelper.initKMoveDrawData(dX);
    }
  }

  /**
   * 主图的手势fling
   */
  @Override
  public void onChartFling(float distanceX) {
    if (mHelper != null) {
      mHelper.initKMoveDrawData(distanceX);
    }
  }
}
