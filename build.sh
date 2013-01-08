#!/bin/bash
SCRIPT_NAME=${0##*/}
SCRIPT_DIR=`dirname $0`
JDK_BASENAME='jdk1.6'
MAVEN_BASENAME='apache-maven-3'

source $SCRIPT_DIR/bin/shell-functions.sh

findJava

findMaven

export JAVA_HOME
export M2_HOME

cd $SCRIPT_DIR
$M2_HOME/bin/mvn clean install
