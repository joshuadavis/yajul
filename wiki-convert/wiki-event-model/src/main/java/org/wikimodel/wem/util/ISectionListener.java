package org.wikimodel.wem.util;

/**
 * @author kotelnikov
 * @param <T> the type of data managed by this listener
 */
public interface ISectionListener<T> {

    void beginLevel(int level, T data);

    void beginSection(int level, T data);

    void beginSectionContent(int level, T data);

    void beginSectionHeader(int level, T data);

    void endLevel(int level, T data);

    void endSection(int level, T data);

    void endSectionContent(int level, T data);

    void endSectionHeader(int level, T data);

}