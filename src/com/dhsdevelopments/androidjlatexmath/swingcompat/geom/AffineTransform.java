package com.dhsdevelopments.androidjlatexmath.swingcompat.geom;

public interface AffineTransform
{
    double getScaleX();

    double getScaleY();

    void scale( double x, double y );

    AffineTransform copy();
}
