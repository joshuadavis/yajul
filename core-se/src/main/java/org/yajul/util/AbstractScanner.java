package org.yajul.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Classpath scanner adapted from Seam.  Finds a 'tag' resource in the classpath and then scans through
 * all the directories and files inside the JAR or directory where the tag resource was found.  This can be used to
 * iterate through all the classes in a particular class path element to find all classes that implement a particular
 * interface, for example.
 * <br>
 * User: josh
 * Date: Mar 6, 2008
 * Time: 6:09:45 PM
 *
 * @author Josh Davis - Adapted to Yajul.
 * @author Thomas Heute
 * @author Gavin King
 * @author Norman Richards
 */
public abstract class AbstractScanner {
    private static final Logger log = Logger.getLogger(AbstractScanner.class.getName());

    private final String resourceName;
    private final ClassLoader classLoader;
    private final Set<String> paths = CollectionUtil.newHashSet();
    private final boolean useParentDirectory;
    private boolean scanned = false;

    /**
     * Scans everything in the classpath where the specified resource is located.
     *
     * @param resourceName resource name used to find a directory or archive to scan
     */
    public AbstractScanner(String resourceName) {
        this(resourceName, Thread.currentThread().getContextClassLoader());
    }

    public AbstractScanner(String resourceName, ClassLoader classLoader) {
        this(resourceName, classLoader, false);
    }

    public AbstractScanner(String resourceName, ClassLoader classLoader, boolean useParentDirectory) {
        this.resourceName = resourceName;
        this.classLoader = classLoader;
        this.useParentDirectory = useParentDirectory;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    protected void scan() {
        if (scanned)
            return;
        if (resourceName == null) {
            for (URL url : getURLsFromClassLoader()) {
                String urlPath = url.getFile();
                if (urlPath.endsWith("/")) {
                    urlPath = urlPath.substring(0, urlPath.length() - 1);
                }
                addPath(urlPath);
            }
        } else {
            try {
                Enumeration<URL> urlEnum = classLoader.getResources(resourceName);
                while (urlEnum.hasMoreElements()) {
                    URL url = urlEnum.nextElement();
                    String urlPath = ResourceUtil.getPath(url);
                    if (ResourceUtil.isFileURL(urlPath)) {
                        urlPath = ResourceUtil.getFilePathFromURL(urlPath);
                    }
                    if (urlPath.indexOf('!') > 0) {
                        urlPath = urlPath.substring(0, urlPath.indexOf('!'));
                    } else {
                        File dirOrArchive = new File(urlPath);
                        // When the tag resource is in the META-INF directory
                        // we will want to search the parent directory.
                        if (useParentDirectory &&
                                resourceName.lastIndexOf('/') > 0) {
                            //for META-INF/someresource.xyz
                            dirOrArchive = dirOrArchive.getParentFile();
                        }
                        urlPath = dirOrArchive.getParent();
                    }
                    addPath(urlPath);
                }
            } catch (IOException ioe) {
                log.log(Level.WARNING, "could not read: " + resourceName, ioe);
                return;
            }
        }

        for (String urlPath : paths) {
            try {
                if (log.isLoggable(Level.FINE))
                    log.log(Level.FINE, "scanning: " + urlPath);
                File file = new File(urlPath);
                if (file.isDirectory()) {
                    handleDirectory(file, null);
                } else {
                    handleArchive(file);
                }
            } catch (IOException ioe) {
                log.log(Level.WARNING, "could not read entries", ioe);
            }
        }
        scanned = true;
    }

    private void addPath(String urlPath) {
        if (log.isLoggable(Level.FINER))
            log.log(Level.FINER, "addPath('" + urlPath + "')");
        paths.add(urlPath);
    }

    protected URL[] getURLsFromClassLoader() {
        return ((URLClassLoader) classLoader).getURLs();
    }

    private void handleArchive(File file) throws IOException {
        if (log.isLoggable(Level.FINER))
            log.log(Level.FINER, "archive: " + file);
        ZipFile zip = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (log.isLoggable(Level.FINER))
                log.log(Level.FINER, "found: " + name);
            handleItem(name);
        }
    }

    private void handleDirectory(File file, String path) {
        if (log.isLoggable(Level.FINER))
            log.log(Level.FINER, "directory: " + file);
        if (file == null)
            throw new IllegalArgumentException("Argument 'file' cannot be null!");
        final File[] files = file.listFiles();
        if (files == null)
            return;
        for (File child : files) {
            String newPath = path == null ?
                    child.getName() : path + '/' + child.getName();
            if (child.isDirectory()) {
                handleDirectory(child, newPath);
            } else {
                handleItem(newPath);
            }
        }
    }

    protected InputStream getResourceAsStream(String name) {
        InputStream stream = classLoader.getResourceAsStream(name);
        if (stream == null)
            log.log(Level.WARNING, "Resource '" + name + "' not found.");
        return stream;
    }

    protected abstract void handleItem(String name);
}
