package brotherhood.onboardcomputer.ui.views.dotsBackground;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Wojtas on 2016-08-09.
 */
public class BackgroundView extends View {
    private int W = 0;
    private int H = 0;
    private boolean refreshThreadRunning = true;
    private DotManager dotManager;

    public BackgroundView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        W = display.getWidth();
        H = display.getHeight();
        dotManager = new DotManager(W, H - getStatusBarHeight());

        startDrawingThread();
    }

    private void startDrawingThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (refreshThreadRunning) {
                    try {
                        Thread.sleep(DotManager.REFRESH_THREAD_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onDraw(Canvas canvas) {
        dotManager.onDraw(canvas);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            dotManager.addDotOnPosition(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    public void destroy(){
        refreshThreadRunning = false;
    }

    public void resume(){
        refreshThreadRunning = true;
        startDrawingThread();
    }
}
