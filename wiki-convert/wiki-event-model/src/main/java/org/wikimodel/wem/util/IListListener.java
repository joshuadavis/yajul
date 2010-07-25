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
 * @author MikhailKotelnikov
 */
public interface IListListener {

    /**
     * Begins of a new row
     * @param treeType TODO
     * @param rowType the type of the row
     */
    void beginRow(char treeType, char rowType);

    /**
     * Notifies about a new tree of the given type
     * 
     * @param type
     */
    void beginTree(char type);

    /**
     * Ends of the row.
     * @param treeType TODO
     * @param rowType the type of the row
     */
    void endRow(char treeType, char rowType);

    /**
     * Ends of the tree of the given type
     * 
     * @param type
     */
    void endTree(char type);

}
