package org.wikimodel.wem;

/**
 * @author MikhailKotelnikov
 */
public class WikiPrinter implements IWikiPrinter {

    private StringBuffer fBuffer;

    /**
     * 
     */
    public WikiPrinter() {
        this(new StringBuffer());
    }

    /**
     * @param buffer
     */
    public WikiPrinter(StringBuffer buffer) {
        fBuffer = buffer;
    }

    public StringBuffer getBuffer() {
        return fBuffer;
    }

    /**
     * @return a new line symbols
     */
    protected String getEol() {
        return "\n";
    }

    /**
     * @see org.wikimodel.wem.IWikiPrinter#print(java.lang.String)
     */
    public void print(String str) {
        fBuffer.append(str);
    }

    /**
     * @see org.wikimodel.wem.IWikiPrinter#println(java.lang.String)
     */
    public void println(String str) {
        fBuffer.append(str);
        fBuffer.append(getEol());
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return fBuffer.toString();
    }

}
