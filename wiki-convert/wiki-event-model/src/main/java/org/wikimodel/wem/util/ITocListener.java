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

/**
 * This listener is used to notify about different structural elements of the
 * table of content.
 * 
 * @author kotelnikov
 */
public interface ITocListener {

    /**
     * 
     */
    void beginItem();

    /**
     * @param headerLevel
     */
    void beginLevel(int headerLevel);

    /**
     * 
     */
    void endItem();

    /**
     * @param headerLevel
     */
    void endLevel(int headerLevel);

}