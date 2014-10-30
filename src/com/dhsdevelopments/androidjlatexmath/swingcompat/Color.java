package com.dhsdevelopments.androidjlatexmath.swingcompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color
{
    public Pattern pattern = Pattern.compile( "^#([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})$" );
    public static final Color BLACK = new Color( 0, 0, 0 );
    public static final Color RED = new Color( 255, 0, 0 );

    public final int r;
    public final int g;
    public final int b;

    public Color( int r, int g, int b ) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color decode( String s ) {
        Matcher matcher = pattern.matcher( s );
        if( !matcher.matches() ) {
            throw new IllegalArgumentException( "Illegal colour string: '" + s + "'" );
        }

        int r = Integer.parseInt( matcher.group( 1 ), 16 );
        int g = Integer.parseInt( matcher.group( 2 ), 16 );
        int b = Integer.parseInt( matcher.group( 3 ), 16 );
        return new Color( r, g, b );
    }
}
