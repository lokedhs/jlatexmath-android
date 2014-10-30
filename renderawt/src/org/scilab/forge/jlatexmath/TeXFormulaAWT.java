package org.scilab.forge.jlatexmath;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TeXFormulaAWT extends TeXFormula
{
    public TeXFormulaAWT( String formula ) {
        super( formula );
    }

    /**
     * Creates a TeXIcon from this TeXFormula using the default TeXFont in the given
     * point size and starting from the given TeX style. If the given integer value
     * does not represent a valid TeX style, the default style
     * TeXConstants.STYLE_DISPLAY will be used.
     *
     * @param style a TeX style constant (from {@link TeXConstants}) to start from
     * @param size the default TeXFont's point size
     * @return the created TeXIcon
     */
    public TeXIcon createTeXIcon(int style, float size) {
        return new TeXIconBuilder().setStyle(style).setSize(size).build();
    }

    public TeXIcon createTeXIcon(int style, float size, int type) {
        return new TeXIconBuilder().setStyle(style).setSize(size).setType(type).build();
    }

    public TeXIcon createTeXIcon(int style, float size, int type, Color fgcolor) {
        return new TeXIconBuilder().setStyle(style).setSize(size).setType(type).setFGColor(fgcolor).build();
    }

    public TeXIcon createTeXIcon(int style, float size, boolean trueValues) {
        return new TeXIconBuilder().setStyle(style).setSize(size).setTrueValues(trueValues).build();
    }

    public TeXIcon createTeXIcon(int style, float size, int widthUnit, float textwidth, int align) {
        return createTeXIcon(style, size, 0, widthUnit, textwidth, align);
    }

    public TeXIcon createTeXIcon(int style, float size, int type, int widthUnit, float textwidth, int align) {
        return new TeXIconBuilder().setStyle(style).setSize(size).setType(type).setWidth(widthUnit, textwidth, align).build();
    }

    public TeXIcon createTeXIcon(int style, float size, int widthUnit, float textwidth, int align, int interlineUnit, float interline) {
        return createTeXIcon(style, size, 0, widthUnit, textwidth, align, interlineUnit, interline);
    }

    public TeXIcon createTeXIcon(int style, float size, int type, int widthUnit, float textwidth, int align, int interlineUnit, float interline) {
        return new TeXIconBuilder().setStyle(style).setSize(size).setType(type).setWidth(widthUnit, textwidth, align).setInterLineSpacing(interlineUnit, interline).build();
    }

    public void createImage(String format, int style, float size, String out, Color bg, Color fg, boolean transparency) {
        TeXIcon icon = createTeXIcon(style, size);
        icon.setInsets(new Insets(1, 1, 1, 1));
        int w = icon.getIconWidth(), h = icon.getIconHeight();

        BufferedImage image = new BufferedImage(w, h, transparency ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        if (bg != null && !transparency) {
            g2.setColor(bg);
            g2.fillRect(0, 0, w, h);
        }

        icon.setForeground(fg);
        icon.paintIcon(null, g2, 0, 0);
        try {
            FileImageOutputStream imout = new FileImageOutputStream(new File(out));
            ImageIO.write( image, format, imout );
            imout.flush();
            imout.close();
        } catch (IOException ex) {
            System.err.println("I/O error : Cannot generate " + out);
        }

        g2.dispose();
    }

    public void createPNG(int style, float size, String out, Color bg, Color fg) {
        createImage("png", style, size, out, bg, fg, bg == null);
    }

    public void createGIF(int style, float size, String out, Color bg, Color fg) {
        createImage("gif", style, size, out, bg, fg, bg == null);
    }

    public void createJPEG(int style, float size, String out, Color bg, Color fg) {
        //There is a bug when a BufferedImage has a component alpha so we disabel it
        createImage("jpeg", style, size, out, bg, fg, false);
    }

    /**
     * @param formula the formula
     * @param style the style
     * @param size the size
     * @return the generated image
     */
    public static Image createBufferedImage(String formula, int style, float size, Color fg, Color bg) throws ParseException {
        TeXFormulaAWT f = new TeXFormulaAWT(formula);
        TeXIcon icon = f.createTeXIcon(style, size);
        icon.setInsets(new Insets(2, 2, 2, 2));
        int w = icon.getIconWidth(), h = icon.getIconHeight();

        BufferedImage image = new BufferedImage(w, h, bg == null ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        if (bg != null) {
            g2.setColor(bg);
            g2.fillRect(0, 0, w, h);
        }

        icon.setForeground(fg == null ? Color.BLACK : fg);
        icon.paintIcon(null, g2, 0, 0);
        g2.dispose();

        return image;
    }

    public Image createBufferedImage(int style, float size, Color fg, Color bg) throws ParseException {
        TeXIcon icon = createTeXIcon(style, size);
        icon.setInsets(new Insets(2, 2, 2, 2));
        int w = icon.getIconWidth(), h = icon.getIconHeight();

        BufferedImage image = new BufferedImage(w, h, bg == null ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        if (bg != null) {
            g2.setColor(bg);
            g2.fillRect(0, 0, w, h);
        }

        icon.setForeground(fg == null ? Color.BLACK : fg);
        icon.paintIcon(null, g2, 0, 0);
        g2.dispose();

        return image;
    }

    /**
     * Apply the Builder pattern instead of using the createTeXIcon(...) factories
     * @author Felix Natter
     *
     */
    public class TeXIconBuilder {
        private Integer style;
        private Float size;
        private Integer type;
        private Color fgcolor;
        private boolean trueValues = false;
        private Integer widthUnit;
        private Float textWidth;
        private Integer align;
        private boolean isMaxWidth = false;
        private Integer interLineUnit;
        private Float interLineSpacing;

        /**
         * Specify the style for rendering the given TeXFormula
         * @param style the style
         * @return the builder, used for chaining
         */
        public TeXIconBuilder setStyle(final int style)
        {
            this.style = style;
            return this;
        }

        /**
         * Specify the font size for rendering the given TeXFormula
         * @param size the size
         * @return the builder, used for chaining
         */
        public TeXIconBuilder setSize(final float size)
        {
            this.size = size;
            return this;
        }

        /**
         * Specify the font type for rendering the given TeXFormula
         * @param type the font type
         * @return the builder, used for chaining
         */
        public TeXIconBuilder setType(final int type)
        {
            this.type = type;
            return this;
        }

        /**
         * Specify the background color for rendering the given TeXFormula
         * @param fgcolor the foreground color
         * @return the builder, used for chaining
         */
        public TeXIconBuilder setFGColor(final Color fgcolor)
        {
            this.fgcolor = fgcolor;
            return this;
        }

        /**
         * Specify the "true values" parameter for rendering the given TeXFormula
         * @param trueValues the "true values" value
         * @return the builder, used for chaining
         */
        public TeXIconBuilder setTrueValues(final boolean trueValues)
        {
            this.trueValues = trueValues;
            return this;
        }

        /**
         * Specify the width of the formula (may be exact or maximum width, see {@link #setIsMaxWidth(boolean)})
         * @param widthUnit the width unit
         * @param textWidth the width
         * @param align the alignment
         * @return the builder, used for chaining
         */
        public TeXIconBuilder setWidth(final int widthUnit, final float textWidth, final int align)
        {
            this.widthUnit = widthUnit;
            this.textWidth = textWidth;
            this.align = align;
            trueValues = true; // TODO: is this necessary?
            return this;
        }

        /**
         * Specifies whether the width is the exact or the maximum width
         * @param isMaxWidth whether the width is a maximum width
         * @return the builder, used for chaining
         */
        public TeXIconBuilder setIsMaxWidth(final boolean isMaxWidth)
        {
            if (widthUnit == null)
            {
                throw new IllegalStateException("Cannot set 'isMaxWidth' without having specified a width!");
            }
            if (isMaxWidth)
            {
                // NOTE: Currently isMaxWidth==true does not work with ALIGN_CENTER or ALIGN_RIGHT (see HorizontalBox ctor)
                // The case (1) we don't support by setting align := ALIGN_LEFT here is this:
                //  \text{hello world\\hello} with align=ALIGN_CENTER (but forced to ALIGN_LEFT) and isMaxWidth==true results in:
                // [hello world]
                // [hello      ]
                // and NOT:
                // [hello world]
                // [   hello   ]
                // However, this case (2) is currently not supported anyway (ALIGN_CENTER with isMaxWidth==false):
                // [  hello world  ]
                // [  hello        ]
                // and NOT:
                // [  hello world  ]
                // [     hello     ]
                // => until (2) is solved, we stick with the hack to set align := ALIGN_LEFT!
                this.align = TeXConstants.ALIGN_LEFT;
            }
            this.isMaxWidth = isMaxWidth;
            return this;
        }

        /**
         * Specify the inter line spacing unit and value. NOTE: this is required for automatic linebreaks to work!
         * @param interLineUnit the unit
         * @param interLineSpacing the value
         * @return the builder, used for chaining
         */
        public TeXIconBuilder setInterLineSpacing(final int interLineUnit, final float interLineSpacing)
        {
            if (widthUnit == null)
            {
                throw new IllegalStateException("Cannot set inter line spacing without having specified a width!");
            }
            this.interLineUnit = interLineUnit;
            this.interLineSpacing = interLineSpacing;
            return this;
        }

        /**
         * Create a TeXIcon from the information gathered by the (chained) setXXX() methods.
         * (see Builder pattern)
         * @return the TeXIcon
         */
        public TeXIcon build()
        {
            if (style == null)
            {
                throw new IllegalStateException("A style is required. Use setStyle()");
            }
            if (size == null)
            {
                throw new IllegalStateException("A size is required. Use setStyle()");
            }
            DefaultTeXFont font = (type == null) ? new DefaultTeXFont(size) : createFont(size, type);
            TeXEnvironment te;
            if (widthUnit != null)
            {
                te = new TeXEnvironment(style, font, widthUnit, textWidth);
            }
            else
            {
                te = new TeXEnvironment(style, font);
            }

            if (interLineUnit != null) {
                te.setInterline(interLineUnit, interLineSpacing);
            }

            Box box = createBox(te);
            TeXIcon ti;
            if (widthUnit != null)
            {
                HorizontalBox hb;
                if (interLineUnit != null)
                {
                    float il = interLineSpacing * SpaceAtom.getFactor(interLineUnit, te);
                    Box b = BreakFormula.split(box, te.getTextwidth(), il);
                    hb = new HorizontalBox(b, isMaxWidth ? b.getWidth() : te.getTextwidth(), align);
                }
                else
                {
                    hb = new HorizontalBox(box, isMaxWidth ? box.getWidth() : te.getTextwidth(), align);
                }
                ti = new TeXIcon(hb, size, trueValues);
            }
            else
            {
                ti = new TeXIcon(box, size, trueValues);
            }
            if (fgcolor != null) {
                ti.setForeground(fgcolor);
            }
            ti.isColored = te.isColored;
            return ti;
        }
    }
}
