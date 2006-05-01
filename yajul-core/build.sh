#!/bin/sh
DIR=`dirname $0`
if [[ -z "$JAVA_HOME" ]] ; then
	echo "ERROR: JAVA_HOME is not defined!"
fi
if [[ -z "$ANT_HOME" ]] ; then
	echo "ERROR: ANT_HOME is not defined!"
fi
$ANT_HOME/bin/ant $@
