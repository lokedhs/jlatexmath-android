package com.dhsdevelopments.mathtest;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MathTest
{
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run() {
                init();
            }
        } );
    }

    private static void init() {
        JFrame frame = new JFrame( "Formula" );

        frame.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );

        JPanel panel = new JPanel( new FlowLayout() );
        frame.setContentPane( panel );

        TeXFormula f = new TeXFormula( "1\\over{\\sqrt{\\sum_{n=0}^{\\infty}{f(n)}}}" );
        TeXIcon icon = f.createTeXIcon( TeXConstants.STYLE_DISPLAY, 40 );
        panel.add( new JLabel( icon ) );

        frame.pack();
        frame.setVisible( true );
    }
}
