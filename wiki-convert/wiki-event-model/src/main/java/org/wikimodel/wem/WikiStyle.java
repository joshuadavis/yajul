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

public class WikiStyle {

    private String fName;

    public WikiStyle(String name) {
        fName = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof WikiStyle))
            return false;
        return fName.equals(((WikiStyle) obj).fName);
    }

    @Override
    public int hashCode() {
        return fName.hashCode();
    }

    @Override
    public String toString() {
        return fName;
    }
}