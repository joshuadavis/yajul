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

/**
 * Common execption for wiki parsers.
 * 
 * @author kotelnikov
 */
public class WikiParserException extends Exception {

    /**
     * The serialize version id
     */
    private static final long serialVersionUID = -4472747025921827543L;

    /**
     * 
     */
    public WikiParserException() {
        super();
    }

    /**
     * @param message
     */
    public WikiParserException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public WikiParserException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public WikiParserException(Throwable cause) {
        super(cause);
    }

}
