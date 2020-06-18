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
import com.guannan.chartmodule.data.LineRectItem;
import com.guannan.chartmodule.data.SubChartData;
import com.guannan.chartmodule.data.TechItem;
import com.guannan.chartmodule.helper.ChartDataSourceHelper;
import com.guannan.chartmodule.helper.ChartTouchHelper;
import com.guannan.chartmodule.helper.TechParamType;
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

  private SubChartData mSubChartData;

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
   * 手指是否长按
   */
  private boolean onLongPress;

  /**
   * 附图类型
   */
  private TechParamType mTechParamType;

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
        DisplayUtils.dip2px(getContext(), 15), DisplayUtils.dip2px(getContext(), 10),
        0);
    super.onSizeChanged(w, h, oldw, oldh);
  }

  public ViewPortHandler getViewPortHandler() {
    return mViewPortHandler;
  }

  @Override
  protected void drawFrame(Canvas canvas) {
    super.drawFrame(canvas);
    if (mToDrawList == null || mToDrawList.isEmpty()) {
      return;
    }

    // 绘制边框和刻度
    drawOutLine(canvas);

    RectF contentRect = mViewPortHandler.mContentRect;

    KLineToDrawItem item = mToDrawList.get(mToDrawList.size() - 1);
    if (mTechParamType == TechParamType.VOLUME) {
      drawVolume(canvas, contentRect);
      if (!onLongPress) {
        drawVolumeDes(canvas, contentRect, item);
      }
    } else if (mTechParamType == TechParamType.MACD) {

      drawMacd(canvas, contentRect);
      if (!onLongPress) {
        drawTechDes(canvas, contentRect, item);
      }
    }

    if (mFocusPoint != null && onLongPress) {

      // 附图实际y轴位置
      float focusY = mFocusPoint.y - getY() - contentRect.top;

      if (contentRect.contains(mFocusPoint.x, focusY)) {
        canvas.drawLine(contentRect.left, focusY, contentRect.right, focusY,
            PaintUtils.FOCUS_LINE_PAINT);
      }
      canvas.drawLine(mFocusPoint.x, contentRect.top, mFocusPoint.x, contentRect.bottom,
          PaintUtils.FOCUS_LINE_PAINT);
      KLineToDrawItem drawItem = mToDrawList.get(mFocusIndex);
      if (mTechParamType == TechParamType.MACD) {
        drawTechDes(canvas, contentRect, drawItem);
      } else if (mTechParamType == TechParamType.VOLUME) {
        drawVolumeDes(canvas, contentRect, drawItem);
      }
    }
  }

  /**
   * 绘制成交量左上角指标
   */
  private void drawVolumeDes(Canvas canvas, RectF contentRect, KLineToDrawItem item) {
    long volume = item.klineItem.volume;
    String volumeDes = "成交量:" + NumFormatUtils.formatBigFloatAll(volume, 2);
    canvas.drawText(volumeDes, contentRect.left, contentRect.top - TEXT_PADDING,
        PaintUtils.TEXT_PAINT);
  }

  /**
   * 绘制MACD左上角指标显示
   */
  public void drawTechDes(Canvas canvas, RectF contentRect, KLineToDrawItem drawItem) {

    TechItem techItem = drawItem.techItem;
    float dif = NumFormatUtils.formatFloat(techItem.dif, 2);
    String difDes = "MACD  DIF:" + dif;
    Rect rectMid = new Rect();
    PaintUtils.TEXT_YELLOW_PAINT.getTextBounds(difDes, 0, difDes.length(), rectMid);
    canvas.drawText(difDes, contentRect.left, contentRect.top - TEXT_PADDING,
        PaintUtils.TEXT_YELLOW_PAINT);

    float dea = NumFormatUtils.formatFloat(techItem.dea, 2);
    String deaDes = "DEA:" + dea;
    Rect rectUpper = new Rect();
    PaintUtils.TEXT_BLUE_PAINT.getTextBounds(deaDes, 0, deaDes.length(), rectUpper);
    canvas.drawText(deaDes, contentRect.left + rectMid.width() + TEXT_PADDING,
        contentRect.top - TEXT_PADDING, PaintUtils.TEXT_BLUE_PAINT);

    float macd = NumFormatUtils.formatFloat(techItem.macd, 2);
    String macdDes = "MACD:" + macd;
    canvas.drawText(macdDes,
        contentRect.left + rectMid.width() + rectUpper.width() + TEXT_PADDING * 2,
        contentRect.top - TEXT_PADDING, PaintUtils.TEXT_PURPLE_PAINT);
  }

  /**
   * 绘制附图MACD
   */
  private void drawMacd(Canvas canvas, RectF contentRect) {
    if (mSubChartData != null) {
      canvas.drawPath(mSubChartData.macdPaths[0], PaintUtils.LINE_BLUE_PAINT);
      canvas.drawPath(mSubChartData.macdPaths[1], PaintUtils.LINE_YELLOW_PAINT);
      List<LineRectItem> macdRects = mSubChartData.macdRects;
      if (macdRects != null && !macdRects.isEmpty()) {
        for (int i = 0; i < macdRects.size(); i++) {
          LineRectItem lineRectItem = macdRects.get(i);
          if (lineRectItem != null) {
            boolean isFall = lineRectItem.isFall;
            if (isFall) {
              canvas.drawRect(lineRectItem.rect, mPaintGreen);
            } else {
              canvas.drawRect(lineRectItem.rect, mPaintRed);
            }
          }
        }
      }
    }
  }

  /**
   * 绘制成交量
   */
  private void drawVolume(Canvas canvas, RectF contentRect) {
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
    if (mExtremeValue != null && mTechParamType == TechParamType.VOLUME) {
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
  public void initData(List<KLineToDrawItem> data, ExtremeValue extremeValue,
      TechParamType techParamType, SubChartData subChartData) {
    this.mToDrawList = data;
    this.mExtremeValue = extremeValue;
    this.mTechParamType = techParamType;
    this.mSubChartData = subChartData;
    invalidateView();
  }

  /**
   * 长按
   */
  @Override
  public void onChartLongPressed(MotionEvent me) {

    onLongPress = true;
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
      onLongPress = false;
    }
    invalidateView();
  }

  @Override
  public void onChartSingleTapped(MotionEvent me) {
    //if (mTechParamType == TechParamType.VOLUME) {
    //  mTechParamType = TechParamType.MACD;
    //} else if (mTechParamType == TechParamType.MACD) {
    //  mTechParamType = TechParamType.VOLUME;
    //}
    //invalidateView();
  }
}
