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

import java.util.ArrayList;
import java.util.List;

/**
 * This is an internal utility class used as a context to keep in memory the
 * current state of parsed trees (list items).
 * 
 * @author MikhailKotelnikov
 */
public final class TreeBuilder<X extends TreeBuilder.IPos<X>> {

    /**
     * This interface identifies position of elements in rows.
     * 
     * @author MikhailKotelnikov
     */
    public interface IPos<X extends IPos<X>> {

        /**
         * @param pos
         * @return <code>true</code> if the underlying data in both positions
         *         are the same
         */
        boolean equalsData(X pos);

        /**
         * @return the position of the node
         */
        int getPos();
    }

    public interface ITreeListener<X extends IPos<X>> {

        void onBeginRow(X n);

        void onBeginTree(X n);

        void onEndRow(X n);

        void onEndTree(X n);
    }

    private static <X extends IPos<X>> void addTail(
        ITreeListener<X> listener,
        List<X> firstArray,
        List<X> secondArray,
        int secondPos,
        boolean openTree) {
        X n = getNode(secondArray, secondPos);
        if (n == null)
            return;
        if (openTree)
            listener.onBeginTree(n);
        listener.onBeginRow(n);
        firstArray.add(n);
        addTail(listener, firstArray, secondArray, secondPos + 1, true);
    }

    private static <X extends IPos<X>> void doAlign(
        ITreeListener<X> listener,
        List<X> firstArray,
        List<X> secondArray,
        boolean expand) {
        boolean newTree = true;
        int f;
        int s;
        int firstLen = firstArray.size();
        int secondLen = secondArray.size();
        for (f = 0, s = 0; f < firstLen && s < secondLen; f++) {
            X first = firstArray.get(f);
            X second = secondArray.get(s);
            int firstPos = first.getPos();
            int secondPos = second.getPos();
            if (firstPos >= secondPos) {
                if (!first.equalsData(second)) {
                    break;
                } else if (s == secondLen - 1) {
                    newTree = false;
                    break;
                }
                s++;
            }
        }
        removeTail(listener, firstArray, f, newTree);
        if (expand) {
            addTail(listener, firstArray, secondArray, s, newTree);
        }
    }

    private static <X extends IPos<X>> X getNode(List<X> list, int pos) {
        return pos < 0 || pos >= list.size() ? null : list.get(pos);
    }

    private static <X extends IPos<X>> void removeTail(
        ITreeListener<X> listener,
        List<X> array,
        int pos,
        boolean closeTree) {
        X node = getNode(array, pos);
        if (node == null)
            return;
        removeTail(listener, array, pos + 1, true);
        listener.onEndRow(node);
        if (closeTree)
            listener.onEndTree(node);
        array.remove(pos);
    }

    /**
     *
     */
    public List<X> fList = new ArrayList<X>();

    private ITreeListener<X> fListener;

    /**
     * 
     */
    public TreeBuilder(ITreeListener<X> listener) {
        super();
        fListener = listener;
    }

    public void align(X pos) {
        List<X> list = new ArrayList<X>();
        if (pos != null)
            list.add(pos);
        align(list);
    }

    public void align(List<X> row) {
        doAlign(fListener, fList, row, true);
    }

    public X getPeek() {
        return !fList.isEmpty() ? fList.get(fList.size() - 1) : null;
    }

    public void trim(X pos) {
        List<X> list = new ArrayList<X>();
        if (pos != null)
            list.add(pos);
        trim(list);
    }

    public void trim(List<X> row) {
        doAlign(fListener, fList, row, false);
    }

}
