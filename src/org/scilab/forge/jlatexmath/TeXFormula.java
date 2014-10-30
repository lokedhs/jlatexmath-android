/* TeXFormula.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */

/* Modified by Calixte Denizet */

package org.scilab.forge.jlatexmath;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.dhsdevelopments.androidjlatexmath.swingcompat.Color;
import com.dhsdevelopments.androidjlatexmath.swingcompat.Graphics2D;
import com.dhsdevelopments.androidjlatexmath.swingcompat.Insets;
import com.dhsdevelopments.androidjlatexmath.swingcompat.image.BufferedImage;
import com.dhsdevelopments.androidjlatexmath.swingcompat.GraphicsEnvironment;
import com.dhsdevelopments.androidjlatexmath.swingcompat.Image;
import com.dhsdevelopments.androidjlatexmath.swingcompat.Toolkit;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

/**
 * Represents a logical mathematical formula that will be displayed (by creating a
 * {@link TeXIcon} from it and painting it) using algorithms that are based on the
 * TeX algorithms.
 * <p>
 * These formula's can be built using the built-in primitive TeX parser
 * (methods with String arguments) or using other TeXFormula objects. Most methods
 * have (an) equivalent(s) where one or more TeXFormula arguments are replaced with
 * String arguments. These are just shorter notations, because all they do is parse
 * the string(s) to TeXFormula's and call an equivalent method with (a) TeXFormula argument(s).
 * Most methods also come in 2 variants. One kind will use this TeXFormula to build
 * another mathematical construction and then change this object to represent the newly
 * build construction. The other kind will only use other
 * TeXFormula's (or parse strings), build a mathematical construction with them and
 * insert this newly build construction at the end of this TeXFormula.
 * Because all the provided methods return a pointer to this (modified) TeXFormula
 * (except for the createTeXIcon method that returns a TeXIcon pointer),
 * method chaining is also possible.
 * <p>
 * <b> Important: All the provided methods modify this TeXFormula object, but all the
 * TeXFormula arguments of these methods will remain unchanged and independent of
 * this TeXFormula object!</b>
 */
public abstract class TeXFormula
{

    public static final String VERSION = "1.0.3";

    public static final int SERIF = 0;
    public static final int SANSSERIF = 1;
    public static final int BOLD = 2;
    public static final int ITALIC = 4;
    public static final int ROMAN = 8;
    public static final int TYPEWRITER = 16;

    // table for putting delimiters over and under formula's,
    // indexed by constants from "TeXConstants"
    private static final String[][] delimiterNames = {
        { "lbrace", "rbrace" },
        { "lsqbrack", "rsqbrack" },
        { "lbrack", "rbrack" },
        { "downarrow", "downarrow" },
        { "uparrow", "uparrow" },
        { "updownarrow", "updownarrow" },
        { "Downarrow", "Downarrow" },
        { "Uparrow", "Uparrow" },
        { "Updownarrow", "Updownarrow" },
        { "vert", "vert" },
        { "Vert", "Vert" }
    };
    static final java.awt.Color defaultColor = new java.awt.Color(0, 0, 0);

    // point-to-pixel conversion
    public static float PIXELS_PER_POINT = 1f;

    // used as second index in "delimiterNames" table (over or under)
    private static final int OVER_DEL = 0;
    private static final int UNDER_DEL = 1;

    // for comparing floats with 0
    protected static final float PREC = 0.0000001f;

    // predefined TeXFormula's
    public static Map<String, TeXFormula> predefinedTeXFormulas = new HashMap<String, TeXFormula>(150);
    public static Map<String, String> predefinedTeXFormulasAsString = new HashMap<String, String>(150);

    // character-to-symbol and character-to-delimiter mappings
    public static String[] symbolMappings = new String[65536];
    public static String[] symbolTextMappings = new String[65536];
    public static String[] symbolFormulaMappings = new String[65536];
    public static Map<Character.UnicodeBlock, FontInfos> externalFontMap = new HashMap<Character.UnicodeBlock, FontInfos>();
    public static float defaultSize = -1;
    public static float magFactor = 0;

    public List<MiddleAtom> middle = new LinkedList<MiddleAtom>();

    protected Map<String, String> jlmXMLMap;
    private TeXParser parser;

    static {
        // character-to-symbol and character-to-delimiter mappings
        TeXFormulaSettingsParser parser = new TeXFormulaSettingsParser();
        parser.parseSymbolMappings(symbolMappings, symbolTextMappings);

        new PredefinedCommands();
        new PredefinedTeXFormulas();
        new PredefMacros();

        parser.parseSymbolToFormulaMappings(symbolFormulaMappings, symbolTextMappings);

        try {
            DefaultTeXFont.registerAlphabet((AlphabetRegistration) Class.forName("org.scilab.forge.jlatexmath.cyrillic.CyrillicRegistration").newInstance());
            DefaultTeXFont.registerAlphabet((AlphabetRegistration) Class.forName("org.scilab.forge.jlatexmath.greek.GreekRegistration").newInstance());
        } catch (Exception e) { }

        //setDefaultDPI();
    }

    /**
     * The constructor is private, as all instantiations has to be done through the <code>make</code> methods.
     */
    protected TeXFormula() {
    }

    public static void addSymbolMappings(String file) throws ResourceParseException {
        FileInputStream in;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ResourceParseException(file, e);
        }
        addSymbolMappings(in, file);
    }

    public static void addSymbolMappings(InputStream in, String name) throws ResourceParseException {
        TeXFormulaSettingsParser tfsp = new TeXFormulaSettingsParser(in, name);
        tfsp.parseSymbolMappings(symbolMappings, symbolTextMappings);
        tfsp.parseSymbolToFormulaMappings(symbolFormulaMappings, symbolTextMappings);
    }

    public static boolean isRegisteredBlock(Character.UnicodeBlock block) {
	return externalFontMap.get(block) != null;
    }

    public static FontInfos getExternalFont(Character.UnicodeBlock block) {
        FontInfos infos = externalFontMap.get(block);
        if (infos == null) {
            infos = new FontInfos("SansSerif", "Serif");
            externalFontMap.put(block, infos);
        }

        return infos;
    }

    public static void registerExternalFont(Character.UnicodeBlock block, String sansserif, String serif) {
        if (sansserif == null && serif == null) {
            externalFontMap.remove(block);
            return;
        }
        externalFontMap.put(block, new FontInfos(sansserif, serif));
        if (block.equals(Character.UnicodeBlock.BASIC_LATIN)) {
            predefinedTeXFormulas.clear();
        }
    }

    public static void registerExternalFont(Character.UnicodeBlock block, String fontName) {
        registerExternalFont(block, fontName, fontName);
    }

    /**
     * Set the DPI of target
     * @param dpi the target DPI
     */
    public static void setDPITarget(float dpi) {
        PIXELS_PER_POINT = dpi / 72f;
    }

    /**
     * Set the default target DPI to the screen dpi (only if we're in non-headless mode)
     */
    public static void setDefaultDPI() {
        if (!GraphicsEnvironment.isHeadless()) {
            setDPITarget( (float)factory.getToolkit().getScreenResolution() );
        }
    }

    // the root atom of the "atom tree" that represents the formula
    public Atom root = null;

    // the current text style
    public String textStyle = null;

    public boolean isColored = false;

    public static TeXFormula getAsText(String text, int alignment) throws ParseException {
        TeXFormula formula = TeXFormula.make();
        if (text == null || "".equals(text)) {
            formula.add(new EmptyAtom());
            return formula;
        }

        String[] arr = text.split( "\n|\\\\\\\\|\\\\cr" );
        ArrayOfAtoms atoms = new ArrayOfAtoms();
        for (String s : arr) {
            TeXFormula f = TeXFormula.make(s, "mathnormal", true, false);
            atoms.add(new RomanAtom(f.root));
            atoms.addRow();
        }
        atoms.checkDimensions();
        formula.add(new MatrixAtom(false, atoms, MatrixAtom.ARRAY, alignment));

        return formula;
    }

    /**
     * @param formula formula
     * @return a partial TeXFormula containing the valid part of formula
     */
    public static TeXFormula getPartialTeXFormula(String formula) {
        TeXFormula f = TeXFormula.make();
        if (formula == null) {
            f.add(new EmptyAtom());
            return f;
        }
        TeXParser parser = new TeXParser(true, formula, f);
        try {
            parser.parse();
        } catch (Exception e) {
            if (f.root == null) {
                f.root = new EmptyAtom();
            }
        }

        return f;
    }

    /**
     * @param b true if the fonts should be registered (Java 1.6 only) to be used
     * with FOP.
     */
    public static void registerFonts(boolean b) {
        DefaultTeXFontParser.registerFonts( b );
    }

    /**
     * Change the text of the TeXFormula and regenerate the root
     *
     * @param ltx the latex formula
     */
    public void setLaTeX(String ltx) throws ParseException {
        parser.reset( ltx );
        if (ltx != null && ltx.length() != 0)
            parser.parse();
    }

    /**
     * Inserts an atom at the end of the current formula
     */
    public TeXFormula add(Atom el) {
        if (el != null) {
            if (el instanceof MiddleAtom)
                middle.add((MiddleAtom) el);
            if (root == null) {
                root = el;
            } else {
                if (!(root instanceof RowAtom)) {
                    root = new RowAtom(root);
                }
                ((RowAtom) root).add(el);
                if (el instanceof TypedAtom) {
                    TypedAtom ta = (TypedAtom) el;
                    int rtype = ta.getRightType();
                    if (rtype == TeXConstants.TYPE_BINARY_OPERATOR || rtype == TeXConstants.TYPE_RELATION) {
                        ((RowAtom) root).add(new BreakMarkAtom());
                    }
                }
            }
        }
        return this;
    }

    /**
     * Parses the given string and inserts the resulting formula
     * at the end of the current TeXFormula.
     *
     * @param s the string to be parsed and inserted
     * @throws ParseException if the string could not be parsed correctly
     * @return the modified TeXFormula
     */
    public TeXFormula add(String s) throws ParseException {
        if (s != null && s.length() != 0) {
            // reset parsing variables
            textStyle = null;
            // parse and add the string
            add(TeXFormula.make(s));
        }
        return this;
    }

    public TeXFormula append(String s) throws ParseException {
        return append(false, s);
    }

    public TeXFormula append(boolean isPartial, String s) throws ParseException {
        if (s != null && s.length() != 0) {
            TeXParser tp = new TeXParser(isPartial, s, this);
            tp.parse();
        }
        return this;
    }

    /**
     * Inserts the given TeXFormula at the end of the current TeXFormula.
     *
     * @param f the TeXFormula to be inserted
     * @return the modified TeXFormula
     */
    public TeXFormula add(TeXFormula f) {
        addImpl (f);
        return this;
    }

    private void addImpl(TeXFormula f) {
        if (f.root != null) {
            // special copy-treatment for Mrow as a root!!
            if (f.root instanceof RowAtom)
                add(new RowAtom(f.root));
            else
                add(f.root);
        }
    }

    public void setLookAtLastAtom(boolean b) {
        if (root instanceof RowAtom)
            ((RowAtom)root).lookAtLastAtom = b;
    }

    public boolean getLookAtLastAtom() {
        if (root instanceof RowAtom)
            return ((RowAtom)root).lookAtLastAtom;
        return false;
    }

    /**
     * Centers the current TeXformula vertically on the axis (defined by the parameter
     * "axisheight" in the resource "DefaultTeXFont.xml".
     *
     * @return the modified TeXFormula
     */
    public TeXFormula centerOnAxis() {
        root = new VCenteredAtom(root);
        return this;
    }

    public static void addPredefinedTeXFormula(InputStream xmlFile) throws ResourceParseException {
        new PredefinedTeXFormulaParser(xmlFile, "TeXFormula").parse(predefinedTeXFormulas);
    }

    public static void addPredefinedCommands(InputStream xmlFile) throws ResourceParseException {
        new PredefinedTeXFormulaParser(xmlFile, "Command").parse(MacroInfo.Commands);
    }

    /**
     * Inserts a strut box (whitespace) with the given width, height and depth (in
     * the given unit) at the end of the current TeXFormula.
     *
     * @param unit a unit constant (from {@link TeXConstants})
     * @param width the width of the strut box
     * @param height the height of the strut box
     * @param depth the depth of the strut box
     * @return the modified TeXFormula
     * @throws InvalidUnitException if the given integer value does not represent
     *                  a valid unit
     */
    public TeXFormula addStrut(int unit, float width, float height, float depth)
        throws InvalidUnitException {
        return add(new SpaceAtom(unit, width, height, depth));
    }

    /**
     * Inserts a strut box (whitespace) with the given width, height and depth (in
     * the given unit) at the end of the current TeXFormula.
     *
     * @param type thinmuskip, medmuskip or thickmuskip (from {@link TeXConstants})
     * @return the modified TeXFormula
     * @throws InvalidUnitException if the given integer value does not represent
     *                  a valid unit
     */
    public TeXFormula addStrut(int type)
        throws InvalidUnitException {
        return add(new SpaceAtom(type));
    }

    /**
     * Inserts a strut box (whitespace) with the given width (in widthUnits), height
     * (in heightUnits) and depth (in depthUnits) at the end of the current TeXFormula.
     *
     * @param widthUnit a unit constant used for the width (from {@link TeXConstants})
     * @param width the width of the strut box
     * @param heightUnit a unit constant used for the height (from TeXConstants)
     * @param height the height of the strut box
     * @param depthUnit a unit constant used for the depth (from TeXConstants)
     * @param depth the depth of the strut box
     * @return the modified TeXFormula
     * @throws InvalidUnitException if the given integer value does not represent
     *                  a valid unit
     */
    public TeXFormula addStrut(int widthUnit, float width, int heightUnit,
                               float height, int depthUnit, float depth) throws InvalidUnitException {
        return add(new SpaceAtom(widthUnit, width, heightUnit, height, depthUnit,
                                 depth));
    }

    /*
     * Convert this TeXFormula into a box, starting form the given style
     */
    protected Box createBox( TeXEnvironment style ) {
        if (root == null)
            return new StrutBox(0, 0, 0, 0);
        else
            return root.createBox(style);
    }

    protected DefaultTeXFont createFont( float size, int type ) {
        DefaultTeXFont dtf = new DefaultTeXFont(size);
        if (type == 0) {
            dtf.setSs(false);
        }
        if ((type & ROMAN) != 0) {
            dtf.setRoman(true);
        }
        if ((type & TYPEWRITER) != 0) {
            dtf.setTt(true);
        }
        if ((type & SANSSERIF) != 0) {
            dtf.setSs(true);
        }
        if ((type & ITALIC) != 0) {
            dtf.setIt(true);
        }
        if ((type & BOLD) != 0) {
            dtf.setBold(true);
        }

        return dtf;
    }

    public void setDEBUG(boolean b) {
        Box.DEBUG = b;
    }

    /**
     * Changes the background color of the <i>current</i> TeXFormula into the given color.
     * By default, a TeXFormula has no background color, it's transparent.
     * The backgrounds of subformula's will be painted on top of the background of
     * the whole formula! Any changes that will be made to this TeXFormula after this
     * background color was set, will have the default background color (unless it will
     * also be changed into another color afterwards)!
     *
     * @param c the desired background color for the <i>current</i> TeXFormula
     * @return the modified TeXFormula
     */
    public TeXFormula setBackground(Color c) {
        if (c != null) {
            if (root instanceof ColorAtom)
                root = new ColorAtom(c, null, (ColorAtom) root);
            else
                root = new ColorAtom(root, c, null);
        }
        return this;
    }

    /**
     * Changes the (foreground) color of the <i>current</i> TeXFormula into the given color.
     * By default, the foreground color of a TeXFormula is the foreground color of the
     * component on which the TeXIcon (created from this TeXFormula) will be painted. The
     * color of subformula's overrides the color of the whole formula.
     * Any changes that will be made to this TeXFormula after this color was set, will be
     * painted in the default color (unless the color will also be changed afterwards into
     * another color)!
     *
     * @param c the desired foreground color for the <i>current</i> TeXFormula
     * @return the modified TeXFormula
     */
    public TeXFormula setColor(Color c) {
        if (c != null) {
            if (root instanceof ColorAtom)
                root = new ColorAtom(null, c, (ColorAtom) root);
            else
                root = new ColorAtom(root, null, c);
        }
        return this;
    }

    /**
     * Sets a fixed left and right type of the current TeXFormula. This has an influence
     * on the glue that will be inserted before and after this TeXFormula.
     *
     * @param leftType atom type constant (from {@link TeXConstants})
     * @param rightType atom type constant (from TeXConstants)
     * @return the modified TeXFormula
     * @throws InvalidAtomTypeException if the given integer value does not represent
     *                  a valid atom type
     */
    public TeXFormula setFixedTypes(int leftType, int rightType)
        throws InvalidAtomTypeException {
        root = new TypedAtom(leftType, rightType, root);
        return this;
    }

    /**
     * Get a predefined TeXFormula.
     *
     * @param name the name of the predefined TeXFormula
     * @return a copy of the predefined TeXFormula
     * @throws FormulaNotFoundException if no predefined TeXFormula is found with the
     *                  given name
     */
    public static TeXFormula get(String name) throws FormulaNotFoundException {
        TeXFormula formula = predefinedTeXFormulas.get( name );
        if (formula == null) {
            String f = predefinedTeXFormulasAsString.get(name);
            if (f == null) {
                throw new FormulaNotFoundException(name);
            }
            TeXFormula tf = TeXFormula.make(f);
            predefinedTeXFormulas.put(name, tf);
            return tf;
        } else {
            return TeXFormula.make(formula);
        }
    }

    static class FontInfos {
        String sansserif;
        String serif;

        FontInfos(String sansserif, String serif) {
            this.sansserif = sansserif;
            this.serif = serif;
        }
    }

    /**
     * Creates an empty TeXFormula.
     *
     */
    public static TeXFormula make() {
        TeXFormula f = factory.make();
        f.parser = new TeXParser("", f, false);
        return f;
    }

    /**
     * Creates a TeXFormula.make by parsing the given string (using a primitive TeX parser).
     *
     * @param s the string to be parsed
     * @throws ParseException if the string could not be parsed correctly
     */
    public static TeXFormula make( String s, Map<String, String> map ) throws ParseException {
        TeXFormula f = factory.make();
        f.jlmXMLMap = map;
        f.parser = new TeXParser(s, f);
        f.parser.parse();
        return f;
    }

    public static TeXFormula make( String s ) {
        return factory.make( s, null );
    }

    public static TeXFormula make( TeXParser tp ) {
        TeXFormula f = factory.make();
        f.jlmXMLMap = tp.formula.jlmXMLMap;
        f.parser = new TeXParser(tp.getIsPartial(), "", f, false);
        return f;
    }

    private static TeXFormula make( String s, String textStyle, boolean firstpass, boolean space ) {
        TeXFormula f = factory.make();
        f.textStyle = textStyle;
        f.parser = new TeXParser(s, f, firstpass, space);
        f.parser.parse();
        return f;
    }

    public static TeXFormula make( String s, boolean firstpass ) throws ParseException {
        TeXFormula f = factory.make();
        f.textStyle = null;
        f.parser = new TeXParser(s, f, firstpass);
        f.parser.parse();
        return f;
    }

    /**
     * Creates a TeXFormula by parsing the given string in the given text style.
     * Used when a text style command was found in the parse string.
     */
    public static TeXFormula make( String s, String textStyle ) throws ParseException {
        TeXFormula f = factory.make();
        f.textStyle = textStyle;
        f.parser = new TeXParser(s, f);
        f.parser.parse();
        return f;
    }

    /**
     * Creates a TeXFormula.make that is a copy of the given TeXFormula.
     * <p>
     * <b>Both TeXFormula's are independent of one another!</b>
     *
     * @param oldFormula the formula to be copied
     */
    public static TeXFormula make(TeXFormula oldFormula) {
        TeXFormula f = factory.make();
        if (oldFormula != null) {
            f.addImpl( oldFormula );
        }
        return f;
    }

    /**
     * Creates a TeXFormula.make by parsing the given string (using a primitive TeX parser).
     *
     * @param s the string to be parsed
     * @throws ParseException if the string could not be parsed correctly
     */
    protected static TeXFormula make( TeXParser tp, String s ) throws ParseException {
        return TeXFormula.make( tp, s, null );
    }

    protected static TeXFormula make( TeXParser tp, String s, boolean firstpass ) throws ParseException {
        TeXFormula f = factory.make();
        f.textStyle = null;
        f.jlmXMLMap = tp.formula.jlmXMLMap;
        boolean isPartial = tp.getIsPartial();
        f.parser = new TeXParser(isPartial, s, f, firstpass);
        if (isPartial) {
            try {
                f.parser.parse();
            } catch (Exception e) { }
        } else {
            f.parser.parse();
        }
        return f;
    }

    /**
     * Creates a TeXFormula by parsing the given string in the given text style.
     * Used when a text style command was found in the parse string.
     */
    protected static TeXFormula make( TeXParser tp, String s, String textStyle ) throws ParseException {
        TeXFormula f = factory.make();
        f.textStyle = textStyle;
        f.jlmXMLMap = tp.formula.jlmXMLMap;
        boolean isPartial = tp.getIsPartial();
        f.parser = new TeXParser(isPartial, s, f);
        if (isPartial) {
            try {
                f.parser.parse();
            } catch (Exception e) {
                if (f.root == null) {
                    f.root = new EmptyAtom();
                }
            }
        } else {
            f.parser.parse();
        }
        return f;
    }

    protected static TeXFormula make( TeXParser tp, String s, String textStyle, boolean firstpass, boolean space ) throws ParseException {
        TeXFormula f = factory.make();
        f.textStyle = textStyle;
        f.jlmXMLMap = tp.formula.jlmXMLMap;
        boolean isPartial = tp.getIsPartial();
        f.parser = new TeXParser(isPartial, s, f, firstpass, space);
        if (isPartial) {
            try {
                f.parser.parse();
            } catch (Exception e) {
                if (f.root == null) {
                    f.root = new EmptyAtom();
                }
            }
        } else {
            f.parser.parse();
        }
        return f;
    }

    private static TeXFormulaFactory factory;

    public static void setFactory( TeXFormulaFactory factory ) {
        TeXFormula.factory = factory;
    }

    public interface TeXFormulaFactory
    {
        TeXFormula make();

        TeXFormula make( String name, String testStyle );

        TeXFormula make( TeXParser tp, String s );

        Toolkit getToolkit();
    }
}
