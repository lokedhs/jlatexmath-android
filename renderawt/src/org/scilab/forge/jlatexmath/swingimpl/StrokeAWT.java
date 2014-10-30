package org.scilab.forge.jlatexmath.swingimpl;

import com.dhsdevelopments.androidjlatexmath.swingcompat.Stroke;

class StrokeAWT implements Stroke
{
    private java.awt.Stroke stroke;
    private java.awt.Stroke backend;

    StrokeAWT( java.awt.Stroke stroke ) {
        this.stroke = stroke;
    }

    java.awt.Stroke getBackend() {
        return backend;
    }
}
