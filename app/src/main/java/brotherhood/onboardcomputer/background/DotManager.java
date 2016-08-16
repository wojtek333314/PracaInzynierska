package brotherhood.onboardcomputer.background;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Wojtas on 2016-08-09.
 */
public class DotManager {
    public static final int REFRESH_THREAD_TIME = 1;
    private static final float RANGE_OF_LINES = .21f;
    private static final float SPEED = 1.825f;
    private static final int DOTS = 10;
    private int W;
    private int H;
    private float maxDistanceBetweenDots = H * RANGE_OF_LINES;
    private ArrayList<Dot> dots;
    private Random random;
    private Paint dotPaint;
    private Paint linePaint;

    public DotManager(int screenX, int screenY) {
        this.W = screenX;
        this.H = screenY;
        this.random = new Random();
        this.dotPaint = new Paint();
        maxDistanceBetweenDots = H * RANGE_OF_LINES;

        dotPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(Color.WHITE);

        linePaint = new Paint();
        linePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.WHITE);

        dots = new ArrayList<>();
        for (int i = 0; i < DOTS; i++) {
            Dot dot = new Dot(random.nextInt(screenX), random.nextInt(screenY));
            dot.setVelocityX(random.nextFloat() * SPEED * (random.nextBoolean() ? -1 : 1));
            dot.setVelocityY(random.nextFloat() * SPEED * (random.nextBoolean() ? -1 : 1));
            dot.setSize(random.nextInt(16) + 5);
            dots.add(dot);
        }
    }

    public void onDraw(Canvas canvas) {
        for (Dot dot1 : dots) {
            dot1.move();
            for (Dot dot2 : dots) {
                if (dot1 != dot2) {
                    float distance = calculateDistance(dot1, dot2);
                    linePaint.setAlpha(255 - (int) ((distance / maxDistanceBetweenDots) * 255));
                    if (distance < maxDistanceBetweenDots)
                        canvas.drawLine(dot1.getX(), dot1.getY()
                                , dot2.getX(), dot2.getY(), linePaint);
                }
            }
        }

        for (Dot dot : dots) {
            dotPaint.setAlpha((int) (dot.getSize() * 12.75f));
            dotPaint.setStrokeWidth(dot.getSize());
            canvas.drawCircle(dot.getX(), dot.getY(), dot.getSize() / 2, dotPaint);
        }

        Iterator<Dot> iter = dots.iterator();
        int dotsToAdd = 0;
        while (iter.hasNext()) {
            Dot dot = iter.next();
            if (isDotOutOfScreen(dot)) {
                iter.remove();
                dotsToAdd++;
            }
        }

        for (int i = 0; i < dotsToAdd; i++)
            addNewDot();

    }

    private float calculateDistance(Dot dot1, Dot dot2) {
        return (float) Math.hypot(dot1.getX() - dot2.getX(), dot1.getY() - dot2.getY());
    }

    private boolean isDotOutOfScreen(Dot dot) {
        return dot.getX() < -W * .05f || dot.getX() > W * 1.05f || dot.getY() > H * 1.05f || dot.getY() < -H * 0.05f;
    }

    private void addNewDot() {
        Dot dot = new Dot(drawXinOutsideArea(), drawYinOutsideArea());
        dot.setVelocityX(random.nextFloat() * SPEED * (random.nextBoolean() ? -1 : 1));
        dot.setVelocityY(random.nextFloat() * SPEED * (random.nextBoolean() ? -1 : 1));
        dot.setSize(random.nextInt(16) + 5);
        dots.add(dot);
    }

    private float drawXinOutsideArea(){
        float result = - random.nextInt((int) (W*0.05f));
        if(random.nextBoolean())
            result = W + result;
        return result;
    }

    private float drawYinOutsideArea(){
        float result = - random.nextInt((int) (H*0.05f));
        if(random.nextBoolean())
            result = H + result;
        return result;
    }

    public void addDot(float x,float y){
        Dot dot = new Dot(x,y);
        dot.setVelocityX(random.nextFloat() * SPEED * (random.nextBoolean() ? -1 : 1));
        dot.setVelocityY(random.nextFloat() * SPEED * (random.nextBoolean() ? -1 : 1));
        dot.setSize(random.nextInt(16) + 5);
        dots.add(dot);
    }
}
