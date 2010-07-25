/**
 * 
 */
package org.wikimodel.wem.util;

/**
 * @author kotelnikov
 */
public class SectionBuilder<T> {

    protected static class TocEntry<T> implements TreeBuilder.IPos<TocEntry<T>> {

        T fData;

        int fDocLevel;

        int fLevel;

        public TocEntry(int docLevel, int level, T data) {
            fDocLevel = docLevel;
            fLevel = level;
            fData = data;
        }

        public boolean equalsData(TocEntry<T> pos) {
            return true;
        }

        public int getPos() {
            return fDocLevel * 10 + fLevel;
        }

    }

    static int fDocLevel;

    TreeBuilder<TocEntry<T>> fBuilder = new TreeBuilder<TocEntry<T>>(
        new TreeBuilder.ITreeListener<TocEntry<T>>() {

            public void onBeginRow(TocEntry<T> n) {
                fListener.beginSection(n.fLevel, n.fData);
            }

            public void onBeginTree(TocEntry<T> n) {
                fListener.beginLevel(n.fLevel, n.fData);
            }

            public void onEndRow(TocEntry<T> n) {
                fListener.endSectionContent(n.fLevel, n.fData);
                fListener.endSection(n.fLevel, n.fData);
            }

            public void onEndTree(TocEntry<T> n) {
                fListener.endLevel(n.fLevel, n.fData);
            }

        });

    ISectionListener<T> fListener;

    public SectionBuilder(ISectionListener<T> listener) {
        fListener = listener;
    }

    public void beginDocument() {
        fDocLevel++;
    }

    public void beginHeader(int level, T data) {
        TocEntry<T> entry = new TocEntry<T>(fDocLevel, level, data);
        fBuilder.align(entry);

        entry = fBuilder.getPeek();
        fListener.beginSectionHeader(entry.fLevel, entry.fData);
    }

    public void endDocument() {
        TocEntry<T> entry = new TocEntry<T>(fDocLevel, 1, null);
        fBuilder.trim(entry);
        fDocLevel--;
    }

    public void endHeader() {
        TocEntry<T> entry = fBuilder.getPeek();
        fListener.endSectionHeader(entry.fLevel, entry.fData);
        fListener.beginSectionContent(entry.fLevel, entry.fData);
    }

}