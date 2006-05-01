#!/bin/sh
DIR=`dirname $0`
source $DIR/set-env.sh
ANT_CP="$ANT_HOME/lib/ant-launcher.jar:$JAVA_HOME/lib/tools.jar:$DIR/lib/junit.jar"
java -cp $ANT_CP -Dant.home=$ANT_HOME org.apache.tools.ant.launch.Launcher $@
