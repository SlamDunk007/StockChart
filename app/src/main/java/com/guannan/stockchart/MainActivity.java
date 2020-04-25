package com.guannan.stockchart;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.guannan.chartmodule.chart.ChartContainer;
import com.guannan.chartmodule.chart.KLineChartView;
import com.guannan.chartmodule.chart.VolumeView;
import com.guannan.chartmodule.data.KLineToDrawItem;
import com.guannan.chartmodule.inter.PressChangeListener;
import com.guannan.chartmodule.helper.ChartDataSourceHelper;
import com.guannan.chartmodule.inter.OnChartDataCountListener;
import com.guannan.chartmodule.utils.DisplayUtils;
import com.guannan.simulateddata.SimulatedManager;
import com.guannan.simulateddata.parser.KLineParser;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements OnChartDataCountListener<List<KLineToDrawItem>>, PressChangeListener {

  private ChartDataSourceHelper mHelper;
  private KLineChartView mKLineChartView;
  private VolumeView mVolumeView;
  private ChartContainer mChartContainer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mChartContainer = findViewById(R.id.chart_container);

    mKLineChartView = new KLineChartView(this);
    mChartContainer.addChildChart(mKLineChartView, 200);
    //mKLineChartView.getViewPortHandler()
    //    .restrainViewPort(DisplayUtils.dip2px(this, 10), DisplayUtils.dip2px(this, 20),
    //        DisplayUtils.dip2px(this, 10), DisplayUtils.dip2px(this, 20));

    mVolumeView = new VolumeView(this);
    mChartContainer.addChildChart(mVolumeView, 100);

    mChartContainer.setPressChangeListener(this);
  }

  public void parse(View view) {
    String kLineData = SimulatedManager.getKLineData(this, SimulatedManager.KLineTotalJson);
    KLineParser parser = new KLineParser(kLineData);
    parser.parseKlineData();

    //mChartContainer.setData(parser.klineList);

    if (mHelper == null) {
      mHelper = new ChartDataSourceHelper(this);
    }
    mHelper.initKDrawData(parser.klineList, mKLineChartView, mVolumeView);
  }

  @Override
  public void onReady(List<KLineToDrawItem> data, float maxValue, float minValue) {
    mKLineChartView.initData(data, maxValue, minValue);
    mVolumeView.initData(data);
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
