package org.yajul.fix.netty;

/**
 * The state of the FIX parser.
 * <br>
 * User: josh
 * Date: May 20, 2009
 * Time: 9:58:21 AM
 */
public enum ParserState {
    INITIAL,
    BEGINSTRING,
    BODYLENGTH,
    BODY,
    CHECKSUM,;
}