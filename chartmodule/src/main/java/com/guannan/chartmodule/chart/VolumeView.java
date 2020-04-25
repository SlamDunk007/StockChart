package com.guannan.chartmodule.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.guannan.chartmodule.R;
import com.guannan.chartmodule.data.KLineToDrawItem;
import java.util.List;

/**
 * @author guannan
 * @date on 2020-04-19 14:23
 * @des 成交量
 */
public class VolumeView extends BaseChartView {

  private Paint mPaintRed;
  private Paint mPaintGreen;
  private List<KLineToDrawItem> mToDrawList;

  public VolumeView(Context context) {
    this(context, null);
  }

  public VolumeView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VolumeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mPaintRed = new Paint();
    mPaintRed.setColor(ContextCompat.getColor(context, R.color.color_fd4331));
    mPaintRed.setStyle(Paint.Style.FILL);

    mPaintGreen = new Paint();
    mPaintGreen.setColor(ContextCompat.getColor(context, R.color.color_05aa3b));
    mPaintGreen.setStyle(Paint.Style.FILL);

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

    for (int i = 0; i < mToDrawList.size(); i++) {
      KLineToDrawItem drawItem = mToDrawList.get(i);
      if (drawItem != null) {
        if (drawItem.isFall) {
          canvas.drawRect(drawItem.volumeRect, mPaintGreen);
        } else {
          canvas.drawRect(drawItem.volumeRect, mPaintRed);
        }
      }
    }
  }

  public void initData(List<KLineToDrawItem> data) {
    this.mToDrawList = data;
    invalidateView();
  }
}
