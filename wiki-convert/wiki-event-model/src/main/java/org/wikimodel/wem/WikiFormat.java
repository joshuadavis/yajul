/*******************************************************************************
 * Copyright (c) 2005,2007 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.wikimodel.wem;

import java.util.HashSet;
import java.util.Set;

/**
 * An immutable set of styles.
 * 
 * @author MikhailKotelnikov
 */
public class WikiFormat {

    public static WikiFormat EMPTY = new WikiFormat();

    private String fClosingTags;

    private String fOpeningTags;

    private Set<WikiStyle> fStyles = new HashSet<WikiStyle>();

    /**
     * 
     */
    public WikiFormat() {
        super();
    }

    /**
     * @param styles
     */
    public WikiFormat(Set<WikiStyle> styles) {
        super();
        fStyles.addAll(styles);
    }

    /**
     * @param style
     */
    public WikiFormat(WikiStyle style) {
        fStyles.add(style);
    }

    /**
     * @param styles
     */
    public WikiFormat(WikiStyle[] styles) {
        super();
        for (WikiStyle style : styles) {
            fStyles.add(style);
        }
    }

    /**
     * Creates a new style set and adds the given style to it.
     * 
     * @param style the style to add
     * @return a new comy of the style set containing the given style
     */
    public WikiFormat addStyle(WikiStyle style) {
        if (fStyles.contains(style))
            return this;
        WikiFormat clone = getClone();
        clone.fStyles.add(style);
        return clone;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof WikiFormat))
            return false;
        WikiFormat set = (WikiFormat) obj;
        return fStyles.equals(set.fStyles);
    }

    /**
     * @return a new clone of this format object
     */
    protected WikiFormat getClone() {
        return new WikiFormat(fStyles);
    }

    /**
     * Returns opening or closing tags corresponding to the given format(it
     * depends on the given flag).
     * 
     * @param open if this flag is <code>true</code> then this method returns
     *        opening tags for this format
     * @return opening or closing tags corresponding to the given format(it
     *         depends on the given flag)
     */
    public String getTags(boolean open) {
        if (fOpeningTags == null) {
            StringBuffer o = new StringBuffer();
            StringBuffer c = new StringBuffer();
            for (WikiStyle style : fStyles) {
                o.append("<").append(style).append(">");
                c.insert(0, ">").insert(0, style).insert(0, "</");
            }
            fOpeningTags = o.toString().intern();
            fClosingTags = c.toString().intern();
        }
        return open ? fOpeningTags : fClosingTags;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return fStyles.hashCode();
    }

    /**
     * @param style the style to check
     * @return <code>true</code> if this format has the specified style
     */
    public boolean hasStyle(WikiStyle style) {
        return fStyles.contains(style);
    }

    /**
     * Creates a new style set which does not contain the specified style.
     * 
     * @param style the style to add
     * @return a new comy of the style set containing the given style
     */
    public WikiFormat removeStyle(WikiStyle style) {
        if (!fStyles.contains(style))
            return this;
        WikiFormat clone = getClone();
        clone.fStyles.remove(style);
        return clone;
    }

    /**
     * Creaetes a new format object where the specified style is switched: if
     * this format contains the given style then the resulting format does not
     * nad vice versa.
     * 
     * @param wikiStyle the style to switch
     * @return a format object where the given style is inversed relatively to
     *         this format
     */
    public WikiFormat switchStyle(WikiStyle wikiStyle) {
        WikiFormat clone = getClone();
        if (clone.fStyles.contains(wikiStyle))
            clone.fStyles.remove(wikiStyle);
        else
            clone.fStyles.add(wikiStyle);
        return clone;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return fStyles.toString();
    }
}
