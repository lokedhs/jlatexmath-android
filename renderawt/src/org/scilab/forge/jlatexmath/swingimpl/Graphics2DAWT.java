package org.scilab.forge.jlatexmath.swingimpl;


import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class Graphics2DAWT implements com.dhsdevelopments.androidjlatexmath.swingcompat.Graphics2D
{
    private Graphics2D backend;

    public Graphics2DAWT( Graphics2D backend ) {
        this.backend = backend;
    }

    private Rectangle2D.Float makeRectangle2DFloat( com.dhsdevelopments.androidjlatexmath.swingcompat.geom.Rectangle2D.Float rectangle ) {
        return new Rectangle2D.Float( rectangle.x, rectangle.y, rectangle.w, rectangle.h );
    }

    @Override
    public com.dhsdevelopments.androidjlatexmath.swingcompat.Stroke getStroke() {
        return new StrokeAWT( backend.getStroke() );
    }

    @Override
    public com.dhsdevelopments.androidjlatexmath.swingcompat.Color getColor() {
        Color colour = backend.getColor();
        return new com.dhsdevelopments.androidjlatexmath.swingcompat.Color( colour.getRed(), colour.getGreen(), colour.getBlue() );
    }

    @Override
    public void setColor( com.dhsdevelopments.androidjlatexmath.swingcompat.Color colour ) {
        backend.setColor( new Color( colour.r, colour.g, colour.b ) );
    }

    @Override
    public void setStroke( com.dhsdevelopments.androidjlatexmath.swingcompat.Stroke stroke ) {
        backend.setStroke( ((StrokeAWT)stroke).getBackend() );
    }

    @Override
    public void fill( com.dhsdevelopments.androidjlatexmath.swingcompat.geom.Rectangle2D.Float rectangle ) {
        backend.fill( makeRectangle2DFloat( rectangle ) );
    }

    @Override
    public void draw( com.dhsdevelopments.androidjlatexmath.swingcompat.geom.Rectangle2D.Float rectangle ) {
        backend.draw( makeRectangle2DFloat( rectangle ) );
    }

    @Override
    public void draw( com.dhsdevelopments.androidjlatexmath.swingcompat.geom.RoundRectangle2D.Float rectangle ) {
        backend.draw( new RoundRectangle2D.Float( rectangle.x, rectangle.y, rectangle.w, rectangle.h, rectangle.arcWidth, rectangle.arcHeight ) );
    }

    @Override
    public void rotate( double v, double x, double y ) {
        backend.rotate( v, x, y );
    }

    @Override
    public com.dhsdevelopments.androidjlatexmath.swingcompat.geom.AffineTransform getTransform() {
        return new AffineTransformAWT( backend.getTransform() );
    }

    @Override
    public void translate( float x, float y ) {
        backend.translate( x, y );
    }

    @Override
    public void scale( float x, float y ) {
        backend.scale( x, y );
    }

    @Override
    public void setTransform( com.dhsdevelopments.androidjlatexmath.swingcompat.geom.AffineTransform oldAt ) {
        backend.setTransform( ((AffineTransformAWT)oldAt).getBackend() );
    }

    @Override
    public void drawArc( int x, int y, int w, int h, int startAngle, int widthAngle ) {
        backend.drawArc( x, y, w, h, startAngle, widthAngle );
    }

    @Override
    public void fillArc( int x, int y, int w, int h, int startAngle, int widthAngle ) {
        backend.fillArc( x, y, w, h, startAngle, widthAngle );
    }

    @Override
    public void draw( com.dhsdevelopments.androidjlatexmath.swingcompat.geom.Line2D.Float line ) {
        backend.draw( new Line2D.Float( line.x1, line.y1, line.x2, line.y2 ) );
    }
}
