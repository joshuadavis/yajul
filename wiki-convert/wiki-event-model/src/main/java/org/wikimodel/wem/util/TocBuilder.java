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
package org.wikimodel.wem.util;

import java.util.Stack;

/**
 * This is an utility class which is used to build tables of content (TOCs).
 * 
 * @author kotelnikov
 */
public class TocBuilder {

    protected int fBaseLevel;

    protected Stack fBaseLevelStack = new Stack();

    protected int fLevel;

    private ITocListener fListener;

    private final int fMaxHeaderDepth;

    private final int fMaxSectionDepth;

    private int fTotalDepth;

    /**
     * @param listener
     */
    public TocBuilder(ITocListener listener) {
        this(listener, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * @param listener
     * @param totalDepth
     */
    public TocBuilder(ITocListener listener, int totalDepth) {
        this(listener, Integer.MAX_VALUE, Integer.MAX_VALUE, totalDepth);
    }

    /**
     * @param listener
     * @param documentDepth
     * @param headerDepth
     */
    public TocBuilder(ITocListener listener, int documentDepth, int headerDepth) {
        this(listener, documentDepth, headerDepth, Integer.MAX_VALUE);

    }

    /**
     * @param listener
     * @param documentDepth
     * @param headerDepth
     * @param totalDepth
     */
    public TocBuilder(
        ITocListener listener,
        int documentDepth,
        int headerDepth,
        int totalDepth) {
        fListener = listener;
        fMaxSectionDepth = documentDepth;
        fMaxHeaderDepth = headerDepth;
        fTotalDepth = totalDepth;
    }

    /**
     * 
     */
    public void beginDocument() {
        fBaseLevelStack.push(new Integer(fBaseLevel));
        fBaseLevel = fLevel;
    }

    /**
     * @param level
     */
    public void beginHeader(int level) {
        setHeaderLevel(level);
        if (checkDepth())
            fListener.beginItem();
    }

    /**
     * @return <code>true</code> if the current element should be shown
     */
    public boolean checkDepth() {
        int documentDepth = fBaseLevelStack.size();
        int headerLevel = getHeaderLevel();
        return documentDepth <= fMaxSectionDepth
            && headerLevel <= fMaxHeaderDepth
            && (documentDepth + headerLevel) <= fTotalDepth;
    }

    /**
     * 
     */
    public void endDocument() {
        setHeaderLevel(0);
        Integer level = (Integer) fBaseLevelStack.pop();
        fBaseLevel = level.intValue();
    }

    /**
     * 
     */
    public void endHeader() {
        if (checkDepth())
            fListener.endItem();
    }

    /**
     * @return the current level of headers
     */
    protected int getHeaderLevel() {
        return fLevel - fBaseLevel;
    }

    protected void setHeaderLevel(int level) {
        while (fLevel > level + fBaseLevel) {
            if (checkDepth())
                fListener.endLevel(getHeaderLevel());
            fLevel--;
        }
        while (fLevel < level + fBaseLevel) {
            fLevel++;
            if (checkDepth())
                fListener.beginLevel(getHeaderLevel());
        }
    }

}
