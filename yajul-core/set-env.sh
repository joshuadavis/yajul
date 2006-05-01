#!/bin/sh
# set-env.sh - Sets up environment variables for yajul-core development
DIR=`dirname $0`
if [[ -z "$JAVA_HOME" ]] ; then
	echo "ERROR: JAVA_HOME is not defined!"
fi
if [[ -z "$ANT_HOME" ]] ; then
	echo "ERROR: ANT_HOME is not defined!"
fi

