#!/bin/bash
SCRIPT_NAME=${0##*/}
SCRIPT_DIR=`dirname $0`
UNAME=`uname`
JDK_BASENAME='jdk1.5'
MAVEN_BASENAME='maven-2'

source $SCRIPT_DIR/bin/shell-functions.sh

findJava

findMaven

cd $SCRIPT_DIR
$M2_HOME/bin/mvn clean package site