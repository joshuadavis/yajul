package org.yajul.io.archiver;

/**
 * Encodes a document id into a file name.
 */
public interface IdEncoder
{
    String encode(Object id);
}
