package com.guannan.stockchart;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.guannan.chartmodule.chart.KLineChartView;
import com.guannan.simulateddata.SimulatedManager;
import com.guannan.simulateddata.parser.KLineParser;

public class MainActivity extends AppCompatActivity {

  private KLineChartView mKLineChartView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mKLineChartView = findViewById(R.id.kline);
  }

  public void parse(View view) {
    String kLineData = SimulatedManager.getKLineData(this, SimulatedManager.KLineTotalJson);
    KLineParser parser = new KLineParser(kLineData);
    parser.parseKlineData();

    mKLineChartView.initData(parser.klineList);
  }
}
