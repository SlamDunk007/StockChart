package com.guannan.chartmodule.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import com.guannan.chartmodule.data.ExtremeValue;
import com.guannan.chartmodule.data.KLineToDrawItem;
import com.guannan.chartmodule.helper.ChartDataSourceHelper;
import com.guannan.chartmodule.helper.ChartTouchHelper;
import com.guannan.chartmodule.utils.DisplayUtils;
import com.guannan.chartmodule.utils.NumFormatUtils;
import com.guannan.chartmodule.utils.PaintUtils;
import java.util.List;

/**
 * @author guannan
 * @date on 2020-03-07 15:56
 * @des K线的主图
 */
public class KMasterChartView extends BaseChartView {

  /**
   * 绘制的蜡烛线数据（主副图的数据）
   */
  private List<KLineToDrawItem> mToDrawList;

  /**
   * 蜡烛线价格最大最小值
   */
  private ExtremeValue mExtremeValue;

  /**
   * 主图文本间隔
   */
  private int TEXT_PADDING;

  /**
   * 刻度间隔
   */
  private int CAL_PADDING;

  /**
   * 十字线长按选中的点
   */
  private int mFocusIndex;

  /**
   * 长按十字线的位置
   */
  private PointF mFocusPoint;

  /**
   * 手指是否抬起
   */
  private boolean onTapUp;

  public KMasterChartView(Context context) {
    this(context, null);
  }

  public KMasterChartView(Context context,
      @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public KMasterChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TEXT_PADDING = DisplayUtils.dip2px(context, 5);
    CAL_PADDING = DisplayUtils.dip2px(context, 3);

    mViewPortHandler.setContentRatio(0.9f);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    mViewPortHandler.restrainViewPort(DisplayUtils.dip2px(getContext(), 10),
        DisplayUtils.dip2px(getContext(), 20), DisplayUtils.dip2px(getContext(), 10),
        0);
    super.onSizeChanged(w, h, oldw, oldh);
  }

  public ViewPortHandler getViewPortHandler() {
    return mViewPortHandler;
  }

  @Override
  protected void drawFrame(Canvas canvas) {
    super.drawFrame(canvas);
    drawOutLine(canvas);
    if (mToDrawList == null || mToDrawList.isEmpty()) {
      return;
    }
    RectF contentRect = mViewPortHandler.mContentRect;
    for (int i = 0; i < mToDrawList.size(); i++) {
      KLineToDrawItem drawItem = mToDrawList.get(i);
      if (drawItem != null) {
        // 绘制蜡烛线日期（只绘制每月第一个交易日）
        if (!TextUtils.isEmpty(drawItem.date)) {
          Rect rect = new Rect();
          PaintUtils.TEXT_PAINT.getTextBounds(drawItem.date, 0, drawItem.date.length(), rect);
          canvas.drawText(drawItem.date, drawItem.rect.centerX() - rect.width() / 2,
              contentRect.bottom + rect.height() + TEXT_PADDING,
              PaintUtils.TEXT_PAINT);
          canvas.drawLine(drawItem.rect.centerX(), contentRect.top, drawItem.rect.centerX(),
              contentRect.bottom, PaintUtils.GRID_INNER_DIVIDER);
        }
        // 绘制蜡烛线
        if (drawItem.isFall) {
          canvas.drawRect(drawItem.rect, mPaintGreen);
          canvas.drawRect(drawItem.shadowRect, mPaintGreen);
        } else {
          canvas.drawRect(drawItem.rect, mPaintRed);
          canvas.drawRect(drawItem.shadowRect, mPaintRed);
        }
      }
    }
    if (mFocusPoint != null && !onTapUp) {
      if (contentRect.contains(mFocusPoint.x, mFocusPoint.y)) {
        canvas.drawLine(contentRect.left, mFocusPoint.y, contentRect.right, mFocusPoint.y,
            PaintUtils.FOCUS_LINE_PAINT);
      }
      canvas.drawLine(mFocusPoint.x, contentRect.top, mFocusPoint.x, contentRect.bottom,
          PaintUtils.FOCUS_LINE_PAINT);
    }
  }

  /**
   * 绘制行情图边框
   */
  private void drawOutLine(Canvas canvas) {
    if (mExtremeValue == null) {
      return;
    }
    RectF contentRect = mViewPortHandler.mContentRect;
    if (contentRect != null) {

      Path path = new Path();
      path.moveTo(contentRect.left, contentRect.top);
      path.lineTo(contentRect.right, contentRect.top);
      path.lineTo(contentRect.right, contentRect.bottom);
      path.lineTo(contentRect.left, contentRect.bottom);
      path.close();
      canvas.drawPath(path, PaintUtils.GRID_DIVIDER);
    }

    // 绘制价格刻度和价格分隔线
    float maxPrice = NumFormatUtils.formatFloat(mExtremeValue.maxPrice, 2);
    float minPrice = NumFormatUtils.formatFloat(mExtremeValue.minPrice, 2);
    Rect rect = new Rect();
    PaintUtils.TEXT_PAINT.getTextBounds(maxPrice + "", 0, String.valueOf(maxPrice).length(), rect);
    canvas.drawText(maxPrice + "", contentRect.left + CAL_PADDING,
        contentRect.top + rect.height() + CAL_PADDING,
        PaintUtils.TEXT_PAINT);
    float perHeight = contentRect.height() / 4;
    float perPrice = NumFormatUtils.formatFloat((maxPrice - minPrice) / 4, 2);

    for (int i = 1; i <= 3; i++) {
      canvas.drawLine(contentRect.left, contentRect.top + perHeight * i, contentRect.right,
          contentRect.top + perHeight * i, PaintUtils.GRID_INNER_DIVIDER);
      float value = NumFormatUtils.formatFloat(maxPrice - perPrice * i, 2);
      canvas.drawText(value + "", contentRect.left + CAL_PADDING,
          contentRect.top + perHeight * i - CAL_PADDING, PaintUtils.TEXT_PAINT);
    }

    canvas.drawText(minPrice + "", contentRect.left + CAL_PADDING, contentRect.bottom - CAL_PADDING,
        PaintUtils.TEXT_PAINT);
  }

  /**
   * 设置主图数据并触发绘制
   */
  public void initData(List<KLineToDrawItem> klineList, ExtremeValue extremeValue) {
    this.mToDrawList = klineList;
    this.mExtremeValue = extremeValue;
    invalidateView();
  }

  /**
   * 长按
   */
  @Override
  public void onChartLongPressed(MotionEvent me) {

    onTapUp = false;
    mFocusPoint = new PointF();
    mFocusPoint.set(me.getX(), me.getY());

    RectF contentRect = mViewPortHandler.mContentRect;

    if (contentRect == null || contentRect.width() <= 0) {
      return;
    }
    mFocusIndex = (int) ((mFocusPoint.x - contentRect.left) * ChartDataSourceHelper.K_D_COLUMNS
        / contentRect.width());
    mFocusIndex = Math.max(0, Math.min(mFocusIndex, ChartDataSourceHelper.K_D_COLUMNS - 1));

    invalidateView();
  }

  @Override
  public void onChartGestureEnd(MotionEvent me,
      ChartTouchHelper.ChartGesture lastPerformedGesture) {
    if (lastPerformedGesture == ChartTouchHelper.ChartGesture.LONG_PRESS) {
      if (mFocusPoint != null) {
        mFocusPoint.set(me.getX(), me.getY());
      }
      onTapUp = true;
    }
    invalidateView();
  }
}
