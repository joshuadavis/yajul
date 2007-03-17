package org.yajul.util;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Prints out a hierarchy with an ASCII art representation.  Comes with a built-in filesystem
 * hierarchy representation (FileNode).
 * <br>User: Joshua Davis
 * Date: Mar 17, 2007
 * Time: 8:35:35 AM
 */
public class AsciiArtTree
{
    /**
     * Display a directory as an ascii art tree.
     *
     * @param dir the directory
     * @param out the output print stream
     */
    public void showTree(File dir, PrintStream out)
    {
        Node node = new FileNode(dir);
        showTree(node, out);
    }

    /**
     * Prints a tree in 'ASCII art' tree form to the specified print stream.
     *
     * @param node The root node of the tree to print.
     * @param out  The print stream.
     */
    public void showTree(Node node, PrintStream out)
    {
        showTree(node, new PrintWriter(out));
    }

    /**
     * Prints a tree in 'ASCII art' tree form to the specified print writer.
     *
     * @param node The root node of the tree to print.
     * @param pw   The print writer.
     */
    public void showTree(Node node, PrintWriter pw)
    {
        LinkedList parents = new LinkedList();
        showTree(parents, node, pw);
        pw.flush();
    }

    private void showTree(List parents, Node node, PrintWriter pw)
    {
        if (node == null)
        {
            pw.println("Node is null!");
            return;
        }

        for (Iterator iterator = parents.iterator(); iterator.hasNext();)
        {
            Node parent = (Node) iterator.next();
            if (parent.getNextSibling() == null)
                pw.print("   ");
            else
                pw.print(" | ");
        }

        if (node.getNextSibling() == null)
            pw.print(" \\-");
        else
            pw.print(" +-");

        String s = node.getLabel();
        pw.println(s);

        List newParents = new LinkedList(parents);
        newParents.add(node);
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
        {
            showTree(newParents, child, pw);
        }
        newParents.clear();
    }

    /**
     * Main program: Show a directory as an ASCII-art tree.
     * <br>
     * Usage: java org.yajul.util.AsciiArtTree {directoryname}
     *
     * @param args command line args
     */
    public static void main(String[] args)
    {
        String dirname = args[0];
        File dir = new File(dirname);
        AsciiArtTree tree = new AsciiArtTree();
        tree.showTree(dir, System.out);
    }

    public static interface Node
    {
        String getLabel();

        Node getNextSibling();

        Node getFirstChild();
    }

    public static class FileNode implements Node
    {
        private File file;
        private FileNode nextSibling;
        private FileNode firstChild;

        public FileNode(File file)
        {
            this(file, null);
        }

        public FileNode(File file, FileNode nextSibling)
        {
            this.file = file;
            this.nextSibling = nextSibling;
            if (file.isDirectory())
            {
                File[] files = file.listFiles();
                Arrays.sort(files, FileComparator.INSTANCE);
                FileNode[] children = new FileNode[files.length];
                for (int i = files.length - 1; i >= 0; i--)
                {
                    // The last child has no sibling.
                    if (i == files.length - 1)
                        children[i] = new FileNode(files[i]);
                    else
                        children[i] = new FileNode(files[i], children[i + 1]);

                    if (i == 0)
                        firstChild = children[i];
                }
            }
        }

        public void setNextSibling(FileNode nextSibling)
        {
            this.nextSibling = nextSibling;
        }

        public String getLabel()
        {
            return file.getName();
        }

        public Node getNextSibling()
        {
            return nextSibling;
        }

        public Node getFirstChild()
        {
            return firstChild;
        }
    }
}
