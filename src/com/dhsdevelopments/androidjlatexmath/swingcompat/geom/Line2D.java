package com.dhsdevelopments.androidjlatexmath.swingcompat.geom;

public class Line2D
{
    public static class Float
    {
        public float x1;
        public float y1;
        public float x2;
        public float y2;

        public void setLine( double x1, double y1, double x2, double y2 ) {
            this.x1 = (float)x1;
            this.y1 = (float)y1;
            this.x2 = (float)x2;
            this.y2 = (float)y2;
        }
    }
}
