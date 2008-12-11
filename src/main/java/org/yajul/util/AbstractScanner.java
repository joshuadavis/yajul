package org.yajul.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
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
    private static final Logger log = LoggerFactory.getLogger(AbstractScanner.class);

    protected String resourceName;
    protected ClassLoader classLoader;
    private boolean scanned = false;
    private Set<String> paths = new HashSet<String>();
    private boolean useParentDirectory = false;

    /**
     * Scans everything in the classpath where the specified resource is located.
     * @param resourceName resource name used to find a directory or archive to scan
     */
    public AbstractScanner(String resourceName) {
        this(resourceName, Thread.currentThread().getContextClassLoader());
    }

    public AbstractScanner(String resourceName, ClassLoader classLoader) {
        this.resourceName = resourceName;
        this.classLoader = classLoader;
    }

    public static String filenameToClassname(String filename) {
        return filename.substring(0, filename.lastIndexOf(".class"))
                .replace('/', '.').replace('\\', '.');
    }

    public static String filenameToPackage(String filename) {
        return filename.substring(0, filename.lastIndexOf(".class"))
                .replace('/', '.').replace('\\', '.');
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
                    String urlPath = url.getFile();
                    urlPath = URLDecoder.decode(urlPath, "UTF-8");
                    if (urlPath.startsWith("file:")) {
                        // On windows urlpath looks like file:/C: on Linux file:/home
                        // substring(5) works for both
                        urlPath = urlPath.substring(5);
                    }
                    if (urlPath.indexOf('!') > 0) {
                        urlPath = urlPath.substring(0, urlPath.indexOf('!'));
                    } else {
                        File dirOrArchive = new File(urlPath);
                        // When the tag resource is in the META-INF directory
                        // we will want to search the parent directory.
                        if (useParentDirectory &&
                                resourceName != null &&
                                resourceName.lastIndexOf('/') > 0) {
                            //for META-INF/someresource.xyz
                            dirOrArchive = dirOrArchive.getParentFile();
                        }
                        urlPath = dirOrArchive.getParent();
                    }
                    addPath(urlPath);
                }
            }
            catch (IOException ioe) {
                log.warn("could not read: " + resourceName, ioe);
                return;
            }
        }

        for (String urlPath : paths) {
            try {
                if (log.isDebugEnabled())
                   log.debug("scanning: " + urlPath);
                File file = new File(urlPath);
                if (file.isDirectory()) {
                    handleDirectory(file, null);
                } else {
                    handleArchive(file);
                }
            }
            catch (IOException ioe) {
                log.warn("could not read entries", ioe);
            }
        }
        scanned = true;
    }

    private void addPath(String urlPath) {
        if (log.isDebugEnabled())
            log.debug("addPath('" + urlPath + "')");
        paths.add(urlPath);
    }

    protected URL[] getURLsFromClassLoader() {
        return ((URLClassLoader) classLoader).getURLs();
    }

    private void handleArchive(File file) throws IOException {
        if (log.isDebugEnabled())
           log.debug("archive: " + file);
        ZipFile zip = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            log.debug("found: " + name);
            handleItem(name);
        }
    }

    private void handleDirectory(File file, String path) {
        if (log.isDebugEnabled())
           log.debug("directory: " + file);
        for (File child : file.listFiles()) {
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
            log.warn("Resource '" + name + "' not found.");
        return stream;
    }
    
    protected abstract void handleItem(String name);
}
