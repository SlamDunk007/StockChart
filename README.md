# StockChart
自定义股票行情图，高仿某币app行情图**（持续更新中！！！）**具体绘制细节参考博客：

https://blog.csdn.net/kemeng7758/article/details/106729748

# 效果图

<img src="https://github.com/SlamDunk007/StockChart/blob/master/chart_dynamic.gif" width="280"/>  <img src="https://github.com/SlamDunk007/StockChart/blob/master/chart1.png" width="280" /> <img src="https://github.com/SlamDunk007/StockChart/blob/master/chart2.png" width="280"/>  <img src="https://github.com/SlamDunk007/StockChart/blob/master/scale_max.png" width="280"/>  <img src="https://github.com/SlamDunk007/StockChart/blob/master/scale_min.png" width="280"/>

# 项目关键类

```java
行情图容器：MarketFigureChart
行情图主图：KMasterChartView
行情图副图：KSubChartView（成交量、MACD）
手势处理：ChartTouchHelper
数据处理：ChartDataSourceHelper
```

# 使用方式（可参考MainActivity当中代码）

（1）布局当中引用

```java
<com.guannan.chartmodule.chart.MarketFigureChart
  android:id="@+id/chart_container"
  android:layout_width="match_parent"
  android:layout_height="wrap_content" />
```

（2）然后在代码当中动态添加即可

```java
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
```
