/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 15, 2002
 * Time: 9:36:13 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.yajul.jdi.CoverageShell;

import java.util.ArrayList;
import java.util.List;

/**
 * This task uses the JDI based CoverageShell / CoverageRunner to determine the methods in the test target set
 * that are actually executed by the test programs.  It requires JUnit and JDI.
 * @author Joshua Davis
 */
public class CoverageTask extends Task
{
    private List targets = new ArrayList();     // The list of targets for the tests.
    private List tests = new ArrayList();       // A list of tests to run.
    private Path classpath;
    private boolean useDefaultExcludes = false;

    /**
     * Enables / disables default excludes.
     */
    public void setUseDefaultExcludes(boolean useDefaultExcludes)
    {
        this.useDefaultExcludes = useDefaultExcludes;
    }

    /**
     * Set the classpath attribute from a reference.
     */
    public void setClasspathRef(Reference ref)
    {
        createClasspath().setRefid(ref);
    }

    /**
     * This creates the object that will be used as a target for the <classpath> nested element.
     */
    public Path createClasspath()
    {
        if (classpath == null)
        {
            classpath = new Path(project);
        }
        return classpath;
    }

    /**
     * This sets the full ClassPath for running the
     * tests.  Instrumented code is determined by
     * the packages parameter.
     */
    public void setClasspath(Path classPath)
    {
        this.classpath = classPath;
    }

    /**
     * Add a set of target files (classes that are the target of the testing).
     */
    public void addTarget(FileSet fileset)
    {
        targets.add(fileset);
    }

    /**
     * Adds a set of tests (JUnit) that will be run.
     */
    public void addTest(FileSet fileset)
    {
//        log("test = " + fileset);
        tests.add(fileset);
    }

    /**
     * Returns the task's classpath, if it was set.  If it was not set, it returns the
     * system classpath.
     */
    public Path getClasspath()
    {
        return (classpath == null) ? Path.systemClasspath : classpath;
    }

    /**
     * Execute the coverage task.
     */
    public void execute() throws BuildException
    {
//        log(getClasspath().toString());

        // Glom all of the file names from the list of file sets.

//        log("tests = " + tests.getClass().getName());

        String[] excludes = new String[0];  // TODO: Have the task parse a list of package excludes (speeds up coverage analysis).
        String[] testFileNames = TaskUtil.getAllFileNames(this,tests);
        String[] targetFileNames = TaskUtil.getAllFileNames(this,targets);
        CoverageShell shell = new CoverageShell(testFileNames,targetFileNames,excludes,useDefaultExcludes,getClasspath().toString());
    }



}