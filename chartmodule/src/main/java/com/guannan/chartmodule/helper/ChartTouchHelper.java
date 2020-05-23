package com.guannan.chartmodule.helper;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.core.view.GestureDetectorCompat;
import com.guannan.chartmodule.inter.IChartGestureListener;
import com.guannan.chartmodule.utils.DataUtils;
import com.guannan.chartmodule.utils.DisplayUtils;

/**
 * @author guannan
 * @date on 2020-03-07 16:00
 * @des 图表的手势操作相关
 */
public class ChartTouchHelper
    extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

  /**
   * 两个手指之间的距离
   */
  private float mSavedDist = 1f;

  /**
   * 两个手指之间在X轴的距离
   */
  private float mSavedXDist = 1f;

  /**
   * 手指fling的最小速度 默认：50px/s
   */
  private int mMinFlingVelocity;

  /**
   * 手指fling的最大速度  默认：8000px/s
   */
  private int mMaxFlingVelocity;

  /**
   * 加速度
   */
  private float mDeceleration;
  private final Handler mHandler;

  /**
   * 计算fling滑动的距离
   */
  private FlingRunnable mFlingRunnable;

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
   * 长按
   */
  protected static final int LONG_PRESS = 3;

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
  private IChartGestureListener mChartGestureListener;

  /**
   * 多指触摸的时候，两个手指之间的中点
   */
  private PointF mTouchPointCenter = new PointF();

  /**
   * 当前手指触摸的开始位置
   */
  private PointF mTouchStartPoint = new PointF();

  /**
   * 缩放距离达到3.5dp认为是缩放
   */
  private float mMinScalePointerDistance;

  /**
   * x轴移动距离，大于18像素认为是移动
   */
  private float mXMoveDist = 18f;

  /**
   * X轴滚动距离
   */
  private float scrollX;

  public ChartTouchHelper(View view) {
    Context context = view.getContext();
    mDetectorCompat = new GestureDetectorCompat(context, this);
    view.setOnTouchListener(this);
    initVelocity(context);
    mHandler = new Handler(Looper.getMainLooper());
  }

  private void initVelocity(Context context) {
    mMinScalePointerDistance = DisplayUtils.dip2px(context, 3.5f);
    ViewConfiguration configuration = ViewConfiguration.get(context);
    mMinFlingVelocity = configuration.getScaledMinimumFlingVelocity();
    mMaxFlingVelocity = configuration.getScaledMaximumFlingVelocity();

    // 加速度
    float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
    mDeceleration = SensorManager.GRAVITY_EARTH
        * 39.37f // inch/meter
        * ppi // pixels per inch
        * ViewConfiguration.getScrollFriction() * 4;
  }

  /**
   * 设置手势监听回调
   */
  public void setChartGestureListener(IChartGestureListener listener) {
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

    mDetectorCompat.onTouchEvent(event);
    // 同时处理单指和多指的手势动作
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        // 记录手势的开始
        startAction(event);
        // 手势触摸的开始位置
        saveTouchStart(event);
        stopFling();
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
          mLastGesture = ChartGesture.DRAG;
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
        } else if (mTouchMode == LONG_PRESS) {
          mLastGesture = ChartGesture.LONG_PRESS;
          if (mChartGestureListener != null) {
            mChartGestureListener.onChartLongPressed(event);
          }
        } else if (mTouchMode == NONE) {
          // 移动之后重置起始位置
          saveTouchStart(event);
          mLastGesture = ChartGesture.DRAG;
          mTouchMode = DRAG;
        }

        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:
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
    mTouchMode = LONG_PRESS;
    if (mChartGestureListener != null) {
      mChartGestureListener.onChartLongPressed(e);
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

  /**
   * @param e1 手指按下的位置
   * @param e2 手指抬起的位置
   * @param velocityX 手指抬起时的x轴的加速度  px/s
   */
  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    mLastGesture = ChartGesture.FLING;
    fling(velocityX, e2.getX() - e1.getX());
    return true;
  }

  private void fling(float velocity, float offset) {
    stopFling();
    if (Math.abs(mDeceleration) > DataUtils.EPSILON) {
      // 根据加速度计算速度减少到0时的时间
      int duration = (int) (1000 * velocity / mDeceleration);
      // 手指抬起时，缓冲的距离
      int totalDistance = (int) ((velocity * velocity) / (mDeceleration + mDeceleration));
      int startX = (int) offset, flingX;
      if (velocity < 0) {
        flingX = startX - totalDistance;
      } else {
        flingX = startX + totalDistance;
      }
      mFlingRunnable = new FlingRunnable(startX, flingX, duration, mHandler, mChartGestureListener);
      mHandler.post(mFlingRunnable);
    }
  }

  private void stopFling() {
    if (mFlingRunnable != null) {
      mFlingRunnable.stop();
      mHandler.removeCallbacks(mFlingRunnable);
    }
  }

  /**
   * @param e1 down的时候event
   * @param e2 move的时候event
   * @param distanceX x轴移动距离：两个move之间差值
   * @param distanceY y轴移动距离
   */
  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    if (mChartGestureListener != null) {
      scrollX -= distanceX;
      // 当X轴移动距离大于18px认为是移动
      if (Math.abs(scrollX) > mXMoveDist) {
        mChartGestureListener.onChartTranslate(e2, scrollX);
        scrollX = 0;
      }
    }
    if (Math.abs(distanceX) > Math.abs(distanceY)) {
      return true;
    } else {
      return false;
    }
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

  private static final class FlingRunnable implements Runnable {
    // 一秒40帧
    private final static int MAX_FPS = 40;
    protected static final int ANIMATION_FPS = 1000 / MAX_FPS;
    private final int fromX;
    private final int toX;
    private final int duration;
    private Handler mHandler;
    private final Interpolator interpolator;
    private IChartGestureListener mGestureListener;
    private boolean continueRunning = true;
    private long startTime = -1;
    private float flingX;

    public FlingRunnable(int startX, int toX, int duration, Handler handler,
        IChartGestureListener gestureListener) {
      this.fromX = startX;
      this.toX = toX;
      this.duration = Math.abs(duration);
      this.interpolator = new DecelerateInterpolator();
      this.mHandler = handler;
      this.mGestureListener = gestureListener;
    }

    @Override
    public void run() {

      long remainTime = 0L;
      if (this.startTime == -1) {
        this.startTime = System.currentTimeMillis();
      } else if (duration > 0) {
        remainTime = (1000 * (System.currentTimeMillis() - startTime))
            / duration;
        remainTime = Math.max(Math.min(remainTime, 1000), 0);
      }
      // 每时间差内移动的距离
      final int deltaX = Math
          .round((fromX - toX)
              * interpolator
              .getInterpolation(remainTime / 1000f));
      int remainDistance = fromX - deltaX;

      if (continueRunning && toX != remainDistance) {
        flingX -= deltaX;
        if (mGestureListener != null && Math.abs(flingX) > 18) {
          mGestureListener.onChartFling(flingX);
          flingX = deltaX;
        }
        mHandler.postDelayed(this, ANIMATION_FPS);
      }
    }

    public void stop() {
      this.continueRunning = false;
      mHandler.removeCallbacks(this);
      if (mGestureListener != null) {
        mGestureListener.onChartFling(0);
      }
    }
  }
}
