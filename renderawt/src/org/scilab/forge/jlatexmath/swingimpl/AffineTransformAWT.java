package org.scilab.forge.jlatexmath.swingimpl;

import java.awt.geom.AffineTransform;

public class AffineTransformAWT implements com.dhsdevelopments.androidjlatexmath.swingcompat.geom.AffineTransform
{
    private AffineTransform backend;

    public AffineTransformAWT( AffineTransform transform ) {
        backend = transform;
    }

    @Override
    public double getScaleX() {
        return backend.getScaleX();
    }

    @Override
    public double getScaleY() {
        return backend.getScaleY();
    }

    @Override
    public void scale( double x, double y ) {
        backend.scale( x, y );
    }

    @Override
    public com.dhsdevelopments.androidjlatexmath.swingcompat.geom.AffineTransform copy() {
        AffineTransform c = (AffineTransform)backend.clone();
        return new AffineTransformAWT( c );
    }

    public AffineTransform getBackend() {
        return backend;
    }
}
