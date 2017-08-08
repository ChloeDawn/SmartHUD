package net.insomniakitten.smarthud.util.interpolation;

import org.lwjgl.util.vector.Vector2f;

/**
 * Derived from: https://github.com/codesoup/android-cubic-bezier-interpolator
 * <p>
 * Creates a cubic bezier curve based on two vectors, and allows simple
 * interpolation of a 0..1 time value.
 */

public class CubicBezierInterpolator implements Interpolator {

    protected Vector2f start, end;

    // Calculation storage to avoid unnecessary instantiation
    protected Vector2f a = new Vector2f(), b = new Vector2f(), c = new Vector2f();

    public CubicBezierInterpolator(Vector2f start, Vector2f end) throws IllegalArgumentException {
        if (start.x < 0 || start.x > 1) {
            throw new IllegalArgumentException("start X value must be in the range [0, 1]");
        }
        if (end.x < 0 || end.x > 1) {
            throw new IllegalArgumentException("end X value must be in the range [0, 1]");
        }
        this.start = start;
        this.end = end;
    }

    public CubicBezierInterpolator(float startX, float startY, float endX, float endY) {
        this(new Vector2f(startX, startY), new Vector2f(endX, endY));
    }

    public CubicBezierInterpolator(double startX, double startY, double endX, double endY) {
        this((float) startX, (float) startY, (float) endX, (float) endY);
    }

    @Override
    public float interpolate(float time) {
        return getBezierCoordinateY(getXForTime(time));
    }

    protected float getBezierCoordinateY(float time) {
        c.y = 3 * start.y;
        b.y = 3 * (end.y - start.y) - c.y;
        a.y = 1 - c.y - b.y;
        return time * (c.y + time * (b.y + time * a.y));
    }

    protected float getXForTime(float time) {
        float x = time;
        float z;
        for (int i = 1; i < 14; i++) {
            z = getBezierCoordinateX(x) - time;
            if (Math.abs(z) < 1e-3) {
                break;
            }
            x -= z / getSlope(x);
        }
        return x;
    }

    private float getSlope(float t) {
        return c.x + t * (2 * b.x + 3 * a.x * t);
    }

    private float getBezierCoordinateX(float time) {
        c.x = 3 * start.x;
        b.x = 3 * (end.x - start.x) - c.x;
        a.x = 1 - c.x - b.x;
        return time * (c.x + time * (b.x + time * a.x));
    }

}
