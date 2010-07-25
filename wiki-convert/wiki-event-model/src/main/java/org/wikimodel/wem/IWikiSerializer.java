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

import java.io.Reader;

/**
 * This is a common interface for all wiki syntax serializers.
 * 
 */

public interface IWikiSerializer {

    /**
     * @param reader
     * @param listener
     * @throws WikiParserException
     */
    void serialize(Reader reader, IWemListener listener) throws WikiParserException;
}
