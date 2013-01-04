package org.yajul.depcheck;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides simplified dependency analysis checking based on JDepend.
 * <br>
 * User: josh
 * Date: 5/25/12
 * Time: 11:50 AM
 */
public class DependencyAnalyzer
{
    private static final Logger log = Logger.getLogger(DependencyAnalyzer.class.getName());

    private final String moduleName;
    private final Collection<String> filters = new ArrayList<String>();
    private JDepend jdepend;
    private Collection<JavaPackage> packages;

    public DependencyAnalyzer(String moduleName)
    {
        if (moduleName == null || moduleName.isEmpty())
            throw new IllegalArgumentException("moduleName cannot be null or empty!");
        this.moduleName = moduleName;
        addFilter("java.*");
        addFilter("javax.*");
    }

    public void init() throws Exception
    {
        PackageFilter packageFilter = new PackageFilter(filters);
        this.jdepend = new JDepend(packageFilter);

        // We need to try a few different directories, as the test might be running in Maven, or in IDEA. [jsd]
        List<File> path = new ArrayList<File>();
        final String targetClasses = "/target/classes";
        path.add(new File("./" + moduleName + targetClasses));
        path.add(new File("./" + targetClasses ));
        path.add(new File("../" + moduleName + targetClasses ));

        File classDir = null;
        for (File file : path)
        {
            classDir = file;
            if (file.exists() && file.isDirectory())
                break;
        }

        if (classDir == null || !classDir.exists() || !classDir.isDirectory())
            throw new Exception("Cannot find classes for JDepend in " + path);

        log.info("Module " + moduleName + " classes: " + classDir.getAbsolutePath());
        jdepend.addDirectory(classDir.getAbsolutePath());
    }

    public void assertNoCircularDependencies()
    {
        Collection<JavaPackage> packages = getPackages();
        int cycles = 0;
        for (JavaPackage javaPackage : packages)
        {
            if (javaPackage.containsCycle())
            {
                log.log(Level.SEVERE,"Cyclic dependency detected in: " + javaPackage.getName());
                cycles++;
            }
            else
            {
                log.info(javaPackage.getName() + " OK");
            }
        }
        if (cycles > 0)
            throw new AssertionError("Cyclic dependencies detected in " + moduleName);
        log.info("No circular dependencies in " + moduleName + ", yay!");
    }

    private Collection<JavaPackage> getPackages()
    {
        if (jdepend == null)
            throw new IllegalStateException("Dependency analyzer not initialized.  Did you forget to call init()?");
        if (packages == null)
            packages = (Collection<JavaPackage>) jdepend.analyze();
        return packages;
    }


    public void addFilter(String packageFilter)
    {
        filters.add(packageFilter);
    }
}
