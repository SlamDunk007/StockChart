# StockChart
自定义股票行情图，高仿某币app行情图**（持续更新中！！！）**

股票？？数字货币？？都是浮云，没那智商还是好好撸代码吧，啊哈哈哈！今天作为一个嫩绿嫩绿的韭菜，就来用技术征服一下割过自己的股票行情图。

股票行情图中比较复杂的应该当属于蜡烛线（阴阳线），这块手势处理复杂、图表指标复杂、交互复杂、数据处理复杂......总之：复杂！

所以就从今天开始我从0到1打造出这个复杂的行情图！费话不多说，上图！

# 效果图

<img src="https://github.com/SlamDunk007/StockChart/blob/master/stockChart.png" style="zoom:30%;" />

# 行情图绘制步骤

## 1、绘制

这里使用的是Android的canvas进行绘制的，android的canvas真的是特别的强大，为了调高绘制效率，我在这里的绘制进行了修改：

提前创建一个Canvas和Bitmap：

```java
private void initCanvas() {
    repeatNum = 0;
    if (mRealCanvas == null) {
      mRealCanvas = new Canvas();

      Bitmap curBitmap =
          createBitmap(mViewPortHandler.getChartWidth(), mViewPortHandler.getChartHeight(),
              Bitmap.Config.ARGB_8888);
      Bitmap alterBitmap = curBitmap.copy(Bitmap.Config.ARGB_8888, true);
      if (curBitmap != null && alterBitmap != null) {
        mRealCanvas.setBitmap(curBitmap);
        mCurBitmap = curBitmap;
        mAlterBitmap = alterBitmap;
      }
    }
  }
```

接下来在子线程当中进行复杂的绘制流程（为什么在子线程呢？这里先不说，后面会单独出一片文章讲一下这块）

```java
/**
   * 在子线程当中进行绘制
   */
  class DoubleBuffering implements Runnable {

    private final WeakReference<BaseChartView> mChartView;

    public DoubleBuffering(BaseChartView view) {
      mChartView = new WeakReference<>(view);
    }

    @Override
    public synchronized void run() {
      if (mChartView != null) {
        BaseChartView baseChartView = mChartView.get();
        if (baseChartView != null && baseChartView.mRealCanvas != null) {
          baseChartView.drawFrame(baseChartView.mRealCanvas);
          // 绘制完成，通知UI线程绘制mRealBitmap
          Bitmap bitmap = baseChartView.mCurBitmap;
          if (bitmap != null && baseChartView.mHandler != null) {
            baseChartView.mHandler.sendEmptyMessage(baseChartView.REFRESH);
          }
        }
      }
    }
  }
```

然后将子线程绘制完成的bitmap交给View的onDraw()方法的canvas去绘制

```java
@Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mRealBitmap != null) {
      canvas.drawBitmap(mRealBitmap, 0, 0, mPaint);
    }
    if (hasDrawed) {
      hasDrawed = false;
      if (!mHandler.hasMessages(START_PAINT)) {
        Message message = new Message();
        message.what = START_PAINT;
        message.obj = mDoubleBuffering;
        mHandler.sendMessageDelayed(message, 25);
      }
    }
  }
```

然后绘制完成！哈哈哈哈哈！

是不是很简单！！！

但是！！！

怎么可能这么简单！具体看源码吧！

## 2、手势识别

手势识别使用android手势集合GestureDetectorCompat，可以处理掉大部分常用手势，scroll，fling，longPress简直不能更好用！

想知道细节，后面会单独出系列文章来好好讲解这里面的绘制思路！