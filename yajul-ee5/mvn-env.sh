#!/bin/sh

UNAME=`uname`
JDK_BASENAME='jdk1.5'
MAVEN_BASENAME='maven-2'

function findJavaHome
{
    for javadir in $@
    do
	echo "Looking for java in $javadir ..."
        javaname=`ls -1 $javadir | grep $JDK_BASENAME | tail -n 1`
	echo ">>$javaname"
        if [[ ! -z "$javaname" ]] ; then
	    echo "Found $javadir/$javaname"
            return
        fi
    done
    javaname=""
    javadir=""
}    

function findJava
{
    case "$UNAME" in
        CYGWIN*)
            if [ "${JAVA_HOME:-}" == "" ]; then
                findJavaHome '/usr/java' '/cygdrive/c' '/cygdrive/c/java'
                JAVA_HOME="$javadir/$javaname"
            else
                JAVA_HOME=`cygpath --mixed $JAVA_HOME`
            fi
            ;;
        Linux*)
            if [ "${JAVA_HOME:-}" == "" ] ; then
                findJavaHome '/usr/java'
                JAVA_HOME="$javadir/$javaname"
            else
                JAVA_HOME=$JAVA_HOME
            fi
            ;;
        *)
            echo "Unknown shell: $UNAME"
            ;;
    esac

    if [[ -z "$JAVA_HOME" ]] ; then
    	echo "ERROR: Couldn't find java!"
	    exit -1
    fi
    if [[ ! -d $JAVA_HOME ]] ; then
        echo "ERROR: $JAVA_HOME does not exist!  Try setting JAVA_HOME in your ~/.bashrc file or something."
        exit -1
    fi

    if [[ ! -f $JAVA_HOME/lib/tools.jar ]] ; then
        echo "ERROR: tools.jar was not found in $JAVA_HOME/lib !"
        exit -1
    fi
    echo "JAVA_HOME=$JAVA_HOME"
}


function findMavenHome
{
    for mavendir in $@
    do
	echo "Looking for maven in $mavendir ..."
        mavenname=`ls -1 $mavendir | grep $MAVEN_BASENAME | tail -n 1`
        if [[ ! -z "$mavenname" ]] ; then
	    echo "Found $mavendir/$mavenname"
            return
        fi
    done
    mavenname=""
    mavendir=""
}    

function findMaven
{
    case "$UNAME" in
        CYGWIN*)
            if [ "${M2_HOME:-}" == "" ]; then
                findMavenHome '/cygdrive/c' '/cygdrive/c/java'
                M2_HOME="$mavendir/$mavenname"
            else
                M2_HOME=`cygpath --mixed $M2_HOME`
            fi
            ;;
        Linux*)
            if [ "${M2_HOME:-}" == "" ] ; then
                findMavenHome '/opt'
                M2_HOME="$mavendir/$mavenname"
            else
                M2_HOME=$M2_HOME
            fi
            ;;
        *)
            echo "Unknown shell: $UNAME"
            ;;
    esac

    if [[ -z "$M2_HOME" ]] ; then
    	echo "ERROR: Couldn't find maven!"
	    exit -1
    fi
    if [[ ! -d $M2_HOME ]] ; then
        echo "ERROR: $M2_HOME does not exist!  Try setting M2_HOME in your ~/.bashrc file or something."
        exit -1
    fi

    if [[ ! -f $M2_HOME/bin/mvn ]] ; then
        echo "ERROR: maven was not found in $M2_HOME !"
        exit -1
    fi
    echo "M2_HOME=$M2_HOME"
}

findJava

findMaven

export JAVA_HOME
export M2_HOME
export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH

echo "Starting nested shell..."
exec $SHELL
