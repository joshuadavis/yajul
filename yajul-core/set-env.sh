#!/bin/sh
# set-env.sh - Sets up environment variables for yajul-core development
DIR=`dirname $0`
UNAME=`uname`
EXPECTED_JAVA_VERSION=j2sdk1.4.2

function findJavaHome
{
    for javadir in $@
    do
        javaname=`ls -1 $javadir | grep $EXPECTED_JAVA_VERSION | tail -n 1`
        if [[ ! -z "$javaname" ]] ; then
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
                findJavaHome '/cygdrive/c' '/cygdrive/c/java'
                JAVA_HOME="$javadir/$javaname"
            else
                JAVA_HOME=`cygpath --mixed $JAVA_HOME`
            fi
            ;;
        *)
            if [ "${JAVA_HOME:-}" == "" ] ; then
                findJavaHome '/usr/java' '/opt' '/usr/local'
                JAVA_HOME="$javadir/$javaname"
            else
                JAVA_HOME=$JAVA_HOME
            fi
            ;;
    esac

    if [[ -z "$JAVA_HOME" ]] ; then
        echo "ERROR: Couldn't find java!"
        exit -1
    fi
    if [[ ! -d $JAVA_HOME ]] ; then
        echo "ERROR: $JAVA_HOME does not exist!"
        exit -1
    fi

    if [[ ! -f $JAVA_HOME/lib/tools.jar ]] ; then
        echo "ERROR: tools.jar was not found in $JAVA_HOME/lib !"
        exit -1
    fi
}

if [[ -z "$JAVA_HOME" ]] ; then
    findJava
fi

if [[ -z "$ANT_HOME" ]] ; then
	echo "ERROR: ANT_HOME is not defined!"
fi
