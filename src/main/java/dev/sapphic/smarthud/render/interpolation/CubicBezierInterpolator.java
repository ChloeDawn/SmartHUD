package dev.sapphic.smarthud.render.interpolation;

import org.lwjgl.util.vector.Vector2f;

/**
 * Derived from: https://github.com/codesoup/android-cubic-bezier-interpolator
 *
 * <p>Creates a cubic bezier curve based on two vectors, and allows simple interpolation of a 0..1
 * time value.
 */
public final class CubicBezierInterpolator implements Interpolator {
  private final Vector2f start;
  private final Vector2f end;

  private final Vector2f v0 = new Vector2f();
  private final Vector2f v1 = new Vector2f();
  private final Vector2f v2 = new Vector2f();

  public CubicBezierInterpolator(final float x0, final float y0, final float x1, final float y1) {
    if ((x0 < 0) || (x0 > 1)) {
      throw new IllegalArgumentException("start X value must be in the range [0, 1]");
    }

    if ((x1 < 0) || (x1 > 1)) {
      throw new IllegalArgumentException("end X value must be in the range [0, 1]");
    }

    this.start = new Vector2f(x0, y0);
    this.end = new Vector2f(x1, y1);
  }

  @Override
  public float interpolate(final float time) {
    return this.getBezierCoordinateY(this.getXForTime(time));
  }

  private float getBezierCoordinateY(final float time) {
    this.v2.y = 3 * this.start.y;
    this.v1.y = (3 * (this.end.y - this.start.y)) - this.v2.y;
    this.v0.y = 1 - this.v2.y - this.v1.y;

    return time * (this.v2.y + (time * (this.v1.y + (time * this.v0.y))));
  }

  private float getXForTime(final float time) {
    float x = time;

    for (int i = 1; i < 14; ++i) {
      final float z = this.getBezierCoordinateX(x) - time;

      if (Math.abs(z) < 0.001) {
        return x;
      }

      x -= z / this.getSlope(x);
    }

    return x;
  }

  @SuppressWarnings("OverlyComplexArithmeticExpression")
  private float getSlope(final float time) {
    return this.v2.x + (time * ((2 * this.v1.x) + (3 * this.v0.x * time)));
  }

  private float getBezierCoordinateX(final float time) {
    this.v2.x = 3 * this.start.x;
    this.v1.x = (3 * (this.end.x - this.start.x)) - this.v2.x;
    this.v0.x = 1 - this.v2.x - this.v1.x;

    return time * (this.v2.x + (time * (this.v1.x + (time * this.v0.x))));
  }
}
