package com.guannan.chartmodule.gesture;

import android.content.Context;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.GestureDetectorCompat;
import com.guannan.chartmodule.utils.DisplayUtils;

/**
 * @author guannan
 * @date on 2020-03-07 16:00
 * @des 图表的手势操作相关
 */
public class ChartTouchListener
    extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

  /**
   * 两个手指之间的距离
   */
  private float mSavedDist = 1f;

  /**
   * 两个手指之间在X轴的距离
   */
  private float mSavedXDist = 1f;

  public enum ChartGesture {
    NONE, DRAG, X_ZOOM, SINGLE_TAP, DOUBLE_TAP, LONG_PRESS, FLING
  }

  protected static final int NONE = 0;

  /**
   * 拖动
   */
  protected static final int DRAG = 1;

  /**
   * x轴方向缩放
   */
  protected static final int X_ZOOM = 2;

  /**
   * 默认的手势动作
   */
  private ChartGesture mLastGesture = ChartGesture.NONE;

  /**
   * 当前的手势状态
   */
  private int mTouchMode = NONE;

  /**
   * 手势识别构造器
   */
  private final GestureDetectorCompat mDetectorCompat;

  /**
   * 手势监听回调
   */
  private ChartGestureListener mChartGestureListener;

  /**
   * 多指触摸的时候，两个手指之间的中点
   */
  private PointF mTouchPointCenter = new PointF();

  /**
   * 当前手指触摸的开始位置
   */
  private PointF mTouchStartPoint = new PointF();

  /**
   * 移动3dp，认为是drag
   */
  private float mDragTriggerDist;

  /**
   * 缩放距离达到3.5dp认为是缩放
   */
  private float mMinScalePointerDistance;

  /**
   * x轴移动距离，大于18像素认为是移动
   */
  private float mXMoveDist = 18f;

  public ChartTouchListener(View view) {
    Context context = view.getContext();
    mDetectorCompat = new GestureDetectorCompat(context, this);
    view.setOnTouchListener(this);
    mDragTriggerDist = DisplayUtils.dip2px(context, 3);
    mMinScalePointerDistance = DisplayUtils.dip2px(context, 3.5f);
  }

  /**
   * 设置手势监听回调
   */
  public void setChartGestureListener(ChartGestureListener listener) {
    this.mChartGestureListener = listener;
  }

  /**
   * 手势触摸开始
   */
  public void startAction(MotionEvent event) {
    if (mChartGestureListener != null) {
      mChartGestureListener.onChartGestureStart(event, mLastGesture);
    }
  }

  /**
   * 手势触摸结束
   */
  public void endAction(MotionEvent event) {
    if (mChartGestureListener != null) {
      mChartGestureListener.onChartGestureEnd(event, mLastGesture);
    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {

    if (mTouchMode == NONE) {
      mDetectorCompat.onTouchEvent(event);
    }
    // 同时处理单指和多指的手势动作
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        // 记录手势的开始
        startAction(event);
        // 手势触摸的开始位置
        saveTouchStart(event);
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
        if (event.getPointerCount() >= 2) {
          saveTouchStart(event);
          // 两个手指之间在X轴的距离
          mSavedXDist = getXDist(event);
          // 两个手指之间的距离
          mSavedDist = spacing(event);
          // 两个手指之间距离大于10才认为是缩放
          if (mSavedDist > 10f) {
            mTouchMode = X_ZOOM;
          }
          // 计算两个手指之间的中点位置
          midPoint(mTouchPointCenter, event);
        }
        break;
      case MotionEvent.ACTION_MOVE:
        if (mTouchMode == DRAG) {
          float x = event.getX() - mTouchStartPoint.x;
          float y = event.getY() - mTouchStartPoint.y;
          mLastGesture = ChartGesture.DRAG;
          if (Math.abs(x) > mXMoveDist && mChartGestureListener != null) {
            // 在X轴和Y轴上移动的距离
            mChartGestureListener.onChartTranslate(event, x, y);
          }
        } else if (mTouchMode == X_ZOOM) {
          if (event.getPointerCount() >= 2) {

            // 手指移动的距离
            float totalDist = spacing(event);

            if (totalDist > mMinScalePointerDistance) {
              if (mTouchMode == X_ZOOM) {
                mLastGesture = ChartGesture.X_ZOOM;
                float xDist = getXDist(event);
                float scaleX = xDist / mSavedXDist;
                if (mChartGestureListener != null) {
                  mChartGestureListener.onChartScale(event, scaleX, 1);
                }
              }
            }
          }
        } else if (mTouchMode == NONE
            && Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(),
            mTouchStartPoint.y)) > mDragTriggerDist) {
          mLastGesture = ChartGesture.DRAG;
          mTouchMode = DRAG;
        }

        break;
      case MotionEvent.ACTION_UP:
        endAction(event);
        break;
      case MotionEvent.ACTION_POINTER_UP:

        break;
      case MotionEvent.ACTION_CANCEL:
        mTouchMode = NONE;
        endAction(event);
        break;
    }

    return true;
  }

  @Override
  public boolean onDoubleTap(MotionEvent e) {
    mLastGesture = ChartGesture.DOUBLE_TAP;
    if (mChartGestureListener != null) {
      mChartGestureListener.onChartDoubleTapped(e);
    }
    return super.onDoubleTap(e);
  }

  @Override
  public void onLongPress(MotionEvent e) {
    mLastGesture = ChartGesture.LONG_PRESS;
    if (mChartGestureListener != null) {
      mChartGestureListener.onChartDoubleTapped(e);
    }
  }

  @Override
  public boolean onSingleTapUp(MotionEvent e) {
    mLastGesture = ChartGesture.SINGLE_TAP;
    if (mChartGestureListener != null) {
      mChartGestureListener.onChartSingleTapped(e);
    }
    return super.onSingleTapUp(e);
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    mLastGesture = ChartGesture.FLING;
    if (mChartGestureListener != null) {
      mChartGestureListener.onChartFling(e1, e2, velocityX, velocityY);
    }
    return super.onFling(e1, e2, velocityX, velocityY);
  }

  /**
   * 计算两个手指在X轴方向的距离
   */
  private static float getXDist(MotionEvent e) {
    float x = Math.abs(e.getX(0) - e.getX(1));
    return x;
  }

  /**
   * 计算两个手指之间真正的距离
   */
  private static float spacing(MotionEvent event) {
    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);
    return (float) Math.sqrt(x * x + y * y);
  }

  /**
   * 计算两个手指触摸的中点
   */
  private static void midPoint(PointF point, MotionEvent event) {
    float x = event.getX(0) + event.getX(1);
    float y = event.getY(0) + event.getY(1);
    point.x = (x / 2f);
    point.y = (y / 2f);
  }

  /**
   * 当前手机触摸的开始位置
   */
  private void saveTouchStart(MotionEvent event) {
    mTouchStartPoint.x = event.getX();
    mTouchStartPoint.y = event.getY();
  }

  /**
   * 返回两个点之间的距离
   */
  protected static float distance(float eventX, float startX, float eventY, float startY) {
    float dx = eventX - startX;
    float dy = eventY - startY;
    return (float) Math.sqrt(dx * dx + dy * dy);
  }
}
