package com.guannan.chartmodule.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.guannan.chartmodule.R;
import com.guannan.chartmodule.data.KLineToDrawItem;
import com.guannan.chartmodule.gesture.ChartGestureListener;
import com.guannan.chartmodule.gesture.ChartTouchListener;
import com.guannan.chartmodule.help.ChartDataSourceHelper;
import com.guannan.chartmodule.utils.LogUtils;
import com.guannan.simulateddata.entity.KLineItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guannan
 * @date on 2020-03-07 15:56
 * @des K线的主要实现类
 */
public class KLineChartView extends BaseChartView implements ChartGestureListener {

  private ChartTouchListener mChartLineTouchListener;
  private Paint mPaintRed;
  private ChartDataSourceHelper mHelper;
  private Paint mPaintGreen;

  public KLineChartView(Context context) {
    this(context, null);
  }

  public KLineChartView(Context context,
      @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public KLineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mChartLineTouchListener = new ChartTouchListener(this);
    mChartLineTouchListener.setChartGestureListener(this);

    mPaintRed = new Paint();
    mPaintRed.setColor(ContextCompat.getColor(context, R.color.color_fd4331));

    mPaintGreen = new Paint();
    mPaintGreen.setColor(ContextCompat.getColor(context, R.color.color_05aa3b));
  }

  @Override
  protected void drawFrame(Canvas canvas) {
    super.drawFrame(canvas);
    if (mHelper != null) {
      List<KLineToDrawItem> kLineItems = mHelper.kLineItems;
      for (int i = 0; i < kLineItems.size(); i++) {
        KLineToDrawItem drawItem = kLineItems.get(i);
        if (drawItem != null) {
          if (drawItem.isFall) {
            canvas.drawRect(drawItem.rect, mPaintGreen);
            canvas.drawRect(drawItem.shadowRect, mPaintGreen);
          } else {
            canvas.drawRect(drawItem.rect, mPaintRed);
            canvas.drawRect(drawItem.shadowRect, mPaintRed);
          }
        }
      }
    }
  }

  /**
   * 初始化数据
   */
  public void initData(ArrayList<KLineItem> klineList) {
    if (mHelper == null) {
      mHelper = new ChartDataSourceHelper();
    }
    mHelper.initKDrawData(klineList, mViewPortHandler);
  }

  @Override
  public void onChartGestureStart(MotionEvent me,
      ChartTouchListener.ChartGesture lastPerformedGesture) {
    LogUtils.d("触摸开始");
  }

  @Override
  public void onChartGestureEnd(MotionEvent me,
      ChartTouchListener.ChartGesture lastPerformedGesture) {
    LogUtils.d("触摸结束");
  }

  @Override
  public void onChartLongPressed(MotionEvent me) {

  }

  @Override
  public void onChartDoubleTapped(MotionEvent me) {

  }

  @Override
  public void onChartSingleTapped(MotionEvent me) {

  }

  @Override
  public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

  }

  @Override
  public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
    LogUtils.d("x轴缩放系数：" + scaleX);
  }

  @Override
  public void onChartTranslate(MotionEvent me, float dX, float dY) {
    LogUtils.d("x轴移动距离：" + dX);
    if (mHelper != null) {
      mHelper.initKMoveDrawData(dX);
    }
    invalidateView();
  }
}
