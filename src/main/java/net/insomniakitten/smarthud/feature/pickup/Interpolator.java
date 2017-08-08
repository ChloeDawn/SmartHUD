package net.insomniakitten.smarthud.feature.pickup;

public interface Interpolator {

    /**
     * Interpolate a time across a function defined by the implementor.
     * 
     * @param time
     *            A time value, must be 0..1
     * 
     * @return The interpolated value. Can be thought of as 'y' where time is 'x'.
     */
    float interpolate(float time);

    default float interpolate(float min, float max, float x) {
        return interpolate((x - min) / (max - min));
    }

    public static final Interpolator LINEAR = f -> f;

}
