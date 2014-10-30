package com.dhsdevelopments.androidjlatexmath.swingcompat;

import com.dhsdevelopments.androidjlatexmath.swingcompat.geom.AffineTransform;
import com.dhsdevelopments.androidjlatexmath.swingcompat.geom.Line2D;
import com.dhsdevelopments.androidjlatexmath.swingcompat.geom.Rectangle2D;
import com.dhsdevelopments.androidjlatexmath.swingcompat.geom.RoundRectangle2D;

public interface Graphics2D
{
    Stroke getStroke();

    Color getColor();

    void setColor( Color background );

    void setStroke( Stroke stroke );

    void fill( Rectangle2D.Float rectangle );

    void draw( Rectangle2D.Float rectangle );

    void draw( RoundRectangle2D.Float rectangle );

    void rotate( double v, double x, double y );

    AffineTransform getTransform();

    void translate( float x, float y );

    void scale( float x, float y );

    void setTransform( AffineTransform oldAt );

    void drawArc( int x, int y, int w, int h, int startAngle, int widthAngle );

    void fillArc( int x, int y, int w, int h, int startAngle, int widthAngle );

    void draw( Line2D.Float line );
}
