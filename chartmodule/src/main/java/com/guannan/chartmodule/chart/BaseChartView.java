package com.guannan.chartmodule.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.guannan.chartmodule.R;
import com.guannan.chartmodule.helper.ChartTouchHelper;
import com.guannan.chartmodule.inter.ITouchResponseListener;
import com.guannan.chartmodule.utils.PaintUtils;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author guannan
 * @date on 2020-02-21 13:28
 * @des 具体绘制View的基类：（1）采用双缓冲绘制机制 （2）支持在子线程当中进行绘制
 */
public abstract class BaseChartView extends View implements ITouchResponseListener {

  /**
   * 如果创建画布Canvas失败：最大重试次数
   */
  private final int MAX_RETRY_COUNT = 3;

  /**
   * 如果创建画布Canvas失败：重试的次数
   */
  private int repeatNum = 0;

  /**
   * 在子线程当中实际进行绘制的canvas
   */
  private Canvas mRealCanvas;

  /**
   * 在子线程当中实际绘制的bitmap
   */
  private Bitmap mRealBitmap;

  /**
   * 用于交换的Bitmap
   */
  private Bitmap mCurBitmap;
  private Bitmap mAlterBitmap;

  /**
   * 绘制完成通知onDraw绘制realBitmap
   */
  private ChartHandler mHandler;

  /**
   * 开始绘制
   */
  private static final int START_PAINT = 1;

  /**
   * 刷新
   */
  private static final int REFRESH = 2;

  /**
   * 绘制mRealBitmap的画笔
   */
  private Paint mPaint;

  /**
   * 是否开始绘制
   */
  private boolean hasDrawed;

  /**
   * 单一线程的线程池
   */
  private ExecutorService mExecutor;

  private DoubleBuffering mDoubleBuffering;

  /**
   * 行情图尺寸等辅助方法
   */
  protected ViewPortHandler mViewPortHandler;

  /**
   * 红色画笔
   */
  protected Paint mPaintRed;

  /**
   * 绿色画笔
   */
  protected Paint mPaintGreen;

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    initRunnable();
  }

  public BaseChartView(Context context) {
    this(context, null);
  }

  public BaseChartView(Context context,
      @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BaseChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    mPaintRed = new Paint();
    mPaintRed.setColor(ContextCompat.getColor(context, R.color.color_fd4331));
    mPaintRed.setStyle(Paint.Style.FILL);

    mPaintGreen = new Paint();
    mPaintGreen.setColor(ContextCompat.getColor(context, R.color.color_05aa3b));
    mPaintGreen.setStyle(Paint.Style.FILL);

    // 行情图尺寸等辅助方法
    mViewPortHandler = new ViewPortHandler();

    initHandler();

    PaintUtils.init(context);
  }

  private void initRunnable() {
    if (mExecutor == null) {
      mExecutor = Executors.newSingleThreadExecutor();
    }
    if (mDoubleBuffering == null) {
      mDoubleBuffering = new DoubleBuffering(this);
    }
  }

  private void initHandler() {
    mHandler = new ChartHandler(this);
  }

  public abstract void onChartLongPressed(MotionEvent me);

  public abstract void onChartGestureEnd(MotionEvent me,
      ChartTouchHelper.ChartGesture lastPerformedGesture);

  public abstract void onChartSingleTapped(MotionEvent me);

  /**
   * 防止内存泄露的Handler
   */
  private static final class ChartHandler extends Handler {

    private final WeakReference<BaseChartView> mView;

    public ChartHandler(BaseChartView view) {
      mView = new WeakReference<>(view);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
      super.handleMessage(msg);
      if (mView != null) {
        BaseChartView chartView = mView.get();
        if (chartView != null) {
          if (msg.what == chartView.START_PAINT) {
            // 开始绘制
            DoubleBuffering doubleBuffering = (DoubleBuffering) msg.obj;
            if (chartView.mExecutor != null) {
              chartView.mExecutor.execute(doubleBuffering);
            }
            //post(doubleBuffering);
          } else if (msg.what == chartView.REFRESH) {

            if (chartView.mRealBitmap == null) {
              chartView.mRealBitmap = chartView.mCurBitmap;
              chartView.mCurBitmap = chartView.mAlterBitmap;
            } else {
              Bitmap alterBitmap = chartView.mRealBitmap;
              chartView.mRealBitmap = chartView.mCurBitmap;
              chartView.mCurBitmap = alterBitmap;
            }
            chartView.mRealCanvas.setBitmap(chartView.mCurBitmap);
            chartView.invalidate();
          }
        }
      }
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {

    mViewPortHandler.setChartDimens(w, h);

    initCanvas();
    super.onSizeChanged(w, h, oldw, oldh);
  }

  /**
   * 根据当前View的尺寸创建画布
   */
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

  /**
   * 根据当前View的宽高创建画布Canvas
   */
  private Bitmap createBitmap(int width, int height, Bitmap.Config config) {
    Bitmap bitmap = null;
    try {
      repeatNum++;
      if (repeatNum < MAX_RETRY_COUNT) {
        bitmap = Bitmap.createBitmap(width, height, config);
      }
    } catch (OutOfMemoryError e) {
      System.gc();
      System.runFinalization();
      bitmap = createBitmap(width, height, config);
    }
    return bitmap;
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (mHandler != null) {
      mHandler.removeCallbacksAndMessages(null);
    }
    if (mExecutor != null) {
      mExecutor.shutdown();
    }
    if (mRealBitmap != null) {
      if (!mRealBitmap.isRecycled()) {
        mRealBitmap.recycle();
      }
      mRealBitmap = null;
    }

    if (mCurBitmap != null) {
      if (!mCurBitmap.isRecycled()) {
        mCurBitmap.recycle();
      }
      mCurBitmap = null;
    }

    if (mAlterBitmap != null) {
      if (!mAlterBitmap.isRecycled()) {
        mAlterBitmap.recycle();
      }
      mAlterBitmap = null;
    }
  }

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

  /**
   * 子类View继承，真正实现绘制，当前线程在子线程
   */
  protected void drawFrame(Canvas canvas) {
    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
  }

  /**
   * 子类决定刷新视图时机
   */
  public void invalidateView() {
    hasDrawed = true;
    invalidate();
  }

  public String getString(int id) {
    return getContext().getResources().getString(id);
  }

  /**
   * 在子线程当中刷新
   */
  public void postInvalidateView() {
    hasDrawed = true;
    postInvalidate();
  }

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
        mHandler.sendMessage(message);
      }
    }
  }
}
