package com.dhsdevelopments.androidjlatexmath.swingcompat.geom;

public interface Rectangle2D
{
    public static class Float
    {
        public final float x;
        public final float y;
        public final float w;
        private final float h;

        public Float( float x, float y, float w, float h ) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
