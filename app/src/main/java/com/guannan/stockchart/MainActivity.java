package com.guannan.stockchart;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.guannan.chartmodule.chart.MarketFigureChart;
import com.guannan.chartmodule.chart.KMasterChartView;
import com.guannan.chartmodule.chart.KSubChartView;
import com.guannan.chartmodule.data.ExtremeValue;
import com.guannan.chartmodule.data.KLineToDrawItem;
import com.guannan.chartmodule.helper.ChartDataSourceHelper;
import com.guannan.chartmodule.inter.OnChartDataCountListener;
import com.guannan.chartmodule.inter.PressChangeListener;
import com.guannan.simulateddata.SimulatedManager;
import com.guannan.simulateddata.parser.KLineParser;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements OnChartDataCountListener<List<KLineToDrawItem>>, PressChangeListener {

  private ChartDataSourceHelper mHelper;
  private KMasterChartView mKLineChartView;
  private KSubChartView mVolumeView;
  private MarketFigureChart mMarketFigureChart;
  private KSubChartView mVolumeView2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mMarketFigureChart = findViewById(R.id.chart_container);

    mKLineChartView = new KMasterChartView(this);
    mMarketFigureChart.addChildChart(mKLineChartView, 200);

    mVolumeView = new KSubChartView(this);
    mMarketFigureChart.addChildChart(mVolumeView, 100);

    mVolumeView2 = new KSubChartView(this);
    mMarketFigureChart.addChildChart(mVolumeView2, 100);

    mMarketFigureChart.setPressChangeListener(this);
  }

  public void parse(View view) {
    String kLineData = SimulatedManager.getKLineData(this, SimulatedManager.KLineTotalJson);
    KLineParser parser = new KLineParser(kLineData);
    parser.parseKlineData();

    if (mHelper == null) {
      mHelper = new ChartDataSourceHelper(this);
    }
    mHelper.initKDrawData(parser.klineList, mKLineChartView, mVolumeView);
  }

  @Override
  public void onReady(List<KLineToDrawItem> data, ExtremeValue extremeValue) {
    mKLineChartView.initData(data, extremeValue);
    mVolumeView.initData(data, extremeValue);
    mVolumeView2.initData(data, extremeValue);
  }

  @Override
  public void onChartTranslate(MotionEvent me, float dX) {
    if (mHelper != null) {
      mHelper.initKMoveDrawData(dX);
    }
  }

  @Override
  public void onChartFling(float distanceX) {
    if (mHelper != null) {
      mHelper.initKMoveDrawData(distanceX);
    }
  }
}
