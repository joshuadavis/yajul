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
package org.wikimodel.wem.tex;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.wikimodel.wem.IWikiPrinter;
import org.wikimodel.wem.PrintTextListener;
import org.wikimodel.wem.ReferenceHandler;
import org.wikimodel.wem.WikiPageUtil;
import org.wikimodel.wem.WikiParameters;
import org.wikimodel.wem.images.ImageUtil;

/**
 * @author MikhailKotelnikov
 */
public class TexSerializer extends PrintTextListener {

    private static class DocumentContext {

        boolean fFirstRowCell = false;

        boolean fTableHead = false;

        boolean fTableHeadCell = false;
    }

    private DocumentContext fContext;

    private Stack<DocumentContext> fContextStack = new Stack<DocumentContext>();

    /**
     * @param printer
     */
    public TexSerializer(IWikiPrinter printer) {
        super(printer);
    }

    @Override
    public void beginDocument() {
        if (fContext == null) {
            println("\\documentclass{article}");
            println("\\usepackage{graphics} % for pdf, bitmapped graphics files");
            println("\\usepackage{graphicx} % for pdf, bitmapped graphics files");
            println("\\usepackage{epsfig} % for postscript graphics files");
            println("\\begin{document}");
            println();
        }
        fContext = new DocumentContext();
        fContextStack.push(fContext);
    }

    public void beginHeader(int level, WikiParameters params) {
        println();
        print("\\");
        for (int i = 0; i < level - 1; i++)
            print("sub");
        print("section{");
    }

    public void beginList(WikiParameters parameters, boolean ordered) {
        println("\\begin{itemize}");
    }

    public void beginListItem() {
        print(" \\item ");
    }

    public void beginParagraph(WikiParameters params) {
        println("");
    }

    public void beginTable(WikiParameters params) {
        println("\\begin{center}");
        println("\\begin{footnotesize}");
        println("\\begin{tabular}{|p{6cm}|p{5cm}|}\\hline");
        fContext.fTableHead = true;
    }

    public void beginTableCell(boolean tableHead, WikiParameters params) {
        String str = tableHead ? "\\textcolor{white}" : "";
        print(str + params);
        if (tableHead)
            fContext.fTableHeadCell = true;
        if (!fContext.fFirstRowCell)
            print("&");
        fContext.fFirstRowCell = false;
    }

    public void beginTableRow(WikiParameters params) {
        if (fContext.fTableHead)
            print("\\rowcolor{style@lightblue}");
        else
            print("");
        fContext.fFirstRowCell = true;
    }

    public void endDocument() {
        fContextStack.pop();
        fContext = !fContextStack.empty() ? fContextStack.peek() : null;
        if (fContext == null) {
            println("\\end{document}");
        }
    }

    public void endHeader(int level, WikiParameters params) {
        println("}");
    }

    public void endList(WikiParameters parameters, boolean ordered) {
        println("\\end{itemize}");
    }

    public void endListItem() {
        println("");
    }

    public void endParagraph(WikiParameters params) {
        println("");
    }

    public void endQuotationLine() {
        println("");
    }

    public void endTable(WikiParameters params) {
        println("\\end{tabular}");
        println("\\end{footnotesize}");
        println("\\end{center}");

    }

    public void endTableCell(boolean tableHead, WikiParameters params) {
        if (tableHead)
            print("}");

    }

    public void endTableRow(WikiParameters params) {
        println("\\\\\\hline");
        fContext.fTableHead = false;
    }

    /**
     * Returns an input stream with an image corresponding to the specified
     * reference. If there is no image was found then this method should return
     * null. This method is used to define dimensions of images used in the
     * output. Sould be overloaded in sublcasses.
     * 
     * @param ref the image reference
     * @return the input stream with an image
     * @throws IOException
     */
    protected InputStream getImageInput(String ref) throws IOException {
        return null;
    }

    /**
     * Returns a two-value array with the size of the image defined by the given
     * url
     * 
     * @param ref the reference to the image
     * @return a size of an image with the specified url;
     */
    protected int[] getImageSize(String ref) {
        int[] result = null;
        try {
            InputStream input = getImageInput(ref);
            if (input != null) {
                int maxWidth = getMaxImageWidth();
                int maxHeight = getMaxImageHeight();
                return ImageUtil.getImageSize(input, maxWidth, maxHeight);
            }
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * Returns maximal possible image height. This method can be overloaded in
     * subclasses.
     * 
     * @return the maximal possible image height
     */
    protected int getMaxImageHeight() {
        return 300;
    }

    /**
     * Returns maximal possible image width. This method can be overloaded in
     * subclasses.
     * 
     * @return the maximal possible image width
     */
    protected int getMaxImageWidth() {
        return 300;
    }

    @Override
    protected ReferenceHandler newReferenceHandler() {
        return new ReferenceHandler() {

            Map<String, int[]> fImageSizes = new HashMap<String, int[]>();

            @Override
            protected void handleImage(String ref, String label) {
                int[] size;
                if (fImageSizes.containsKey(ref))
                    size = fImageSizes.get(ref);
                else {
                    size = getImageSize(ref);
                    fImageSizes.put(ref, size);
                }

                if (size != null) {
                    // print("\\begin{figure}[htpb]\n");
                    String dim = "[bb=0 0 " + size[0] + " " + size[1] + "]";
                    println("\\includegraphics"
                        + dim
                        + "{"
                        + WikiPageUtil.escapeXmlString(ref)
                        + "}");
                    ref = ref.replaceAll("_", "-");
                    // if (!"".equals(label)) {
                    // println("\\caption{" + label + "}");
                    // println("\\label{fig:" + label + "}");
                    // }
                    // print("\\end{figure}");
                }
            }

            @Override
            protected void handleReference(String ref, String label) {
                print(label);
                print(" (");
                print(ref);
                print(")");
            }

        };
    }

    public void onEscape(String str) {
        print(str);
    }

    public void onLineBreak() {
        println("\\newline");
    }

    public void onNewLine() {
        println("");
    }

    public void onSpace(String str) {
        print(str);

    }

    public void onSpecialSymbol(String str) {
        if (!str.equals("}") && !str.equals("{")) {
            print(str);
        }
    }

    public void onWord(String str) {
        if (fContext.fTableHeadCell) {
            print("{\\bf ");
            fContext.fTableHeadCell = false;
        }
        print(str);
    }

}
