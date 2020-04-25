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
 * @date on 2020-04-19 14:23
 * @des K线的副图
 */
public class KSubChartView extends BaseChartView {

  /**
   * 绘制的蜡烛线数据（主副图的数据）
   */
  private List<KLineToDrawItem> mToDrawList;

  /**
   * 最大值最小值
   */
  private ExtremeValue mExtremeValue;

  /**
   * 主图文本间隔
   */
  private int TEXT_PADDING;

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

  public KSubChartView(Context context) {
    this(context, null);
  }

  public KSubChartView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public KSubChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TEXT_PADDING = DisplayUtils.dip2px(context, 5);
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
    // 绘制边框和刻度
    drawOutLine(canvas);
    if (mToDrawList == null || mToDrawList.isEmpty()) {
      return;
    }

    RectF contentRect = mViewPortHandler.mContentRect;

    for (int i = 0; i < mToDrawList.size(); i++) {
      KLineToDrawItem drawItem = mToDrawList.get(i);
      if (drawItem != null) {
        // 绘制内部分隔线
        if (!TextUtils.isEmpty(drawItem.date)) {
          canvas.drawLine(drawItem.rect.centerX(), contentRect.top, drawItem.rect.centerX(),
              contentRect.bottom, PaintUtils.GRID_INNER_DIVIDER);
        }
        // 绘制成交量柱
        if (drawItem.isFall) {
          canvas.drawRect(drawItem.volumeRect, mPaintGreen);
        } else {
          canvas.drawRect(drawItem.volumeRect, mPaintRed);
        }
      }
    }

    if (mFocusPoint != null && !onTapUp) {
      if(contentRect.contains(mFocusPoint.x,mFocusPoint.y)){
        canvas.drawLine(contentRect.left, mFocusPoint.y, contentRect.right, mFocusPoint.y,
            PaintUtils.FOCUS_LINE_PAINT);
      }
      canvas.drawLine(mFocusPoint.x, contentRect.top, mFocusPoint.x, contentRect.bottom,
          PaintUtils.FOCUS_LINE_PAINT);
    }
  }

  /**
   * 绘制外围边框和刻度
   */
  private void drawOutLine(Canvas canvas) {
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
    canvas.drawLine(contentRect.left, contentRect.centerY(), contentRect.right,
        contentRect.centerY(), PaintUtils.GRID_INNER_DIVIDER);
    // 绘制附图最大刻度
    if (mExtremeValue != null) {
      String maxVolume = NumFormatUtils.formatBigFloatAll(mExtremeValue.maxVolume, 2);
      Rect rect = new Rect();
      PaintUtils.TEXT_PAINT.getTextBounds(maxVolume, 0, maxVolume.length(), rect);
      canvas.drawText(maxVolume, contentRect.left + TEXT_PADDING,
          contentRect.top + rect.height() + TEXT_PADDING,
          PaintUtils.TEXT_PAINT);
    }
  }

  /**
   * 设置副图数据，并触发绘制
   */
  public void initData(List<KLineToDrawItem> data, ExtremeValue extremeValue) {
    this.mToDrawList = data;
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
