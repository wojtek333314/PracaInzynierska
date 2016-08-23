package brotherhood.onboardcomputer.views.dotsBackground;

/**
 * Created by Wojtas on 2016-08-09.
 */
public class Dot {
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private int size = 10;

    public Dot(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void move() {
        x += velocityX;
        y += velocityY;
    }
}
