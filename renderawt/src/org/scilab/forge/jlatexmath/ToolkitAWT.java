package org.scilab.forge.jlatexmath;

import com.dhsdevelopments.androidjlatexmath.swingcompat.Toolkit;

public class ToolkitAWT extends Toolkit
{
    private java.awt.Toolkit instance;

    public ToolkitAWT( java.awt.Toolkit instance ) {
        this.instance = instance;
    }

    public int getScreenResolution() {
        return instance.getScreenResolution();
    }
}
