#!/bin/bash
SCRIPT_NAME=${0##*/}
SCRIPT_DIR=`dirname $0`
UNAME=`uname`
JDK_BASENAME='jdk1.5'
MAVEN_BASENAME='maven-2'

source $SCRIPT_DIR/bin/shell-functions.sh

findJava

findMaven

export JAVA_HOME
export M2_HOME
export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH

echo "Starting nested shell..."
exec $SHELL
