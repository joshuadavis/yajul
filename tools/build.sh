#!/bin/sh
# ----- Verify and Set Required Environment Variables -------------------------
if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi
if [ "$TERM" = "cygwin" ] ; then
  S=';'
else
  S=':'
fi
ANT_HOME=`dirname "$0"`/ant
echo ANT_HOME=$ANT_HOME
# Launch ANT from the tools/ant/lib directory.
java -classpath $ANT_HOME/lib/ant-launcher.jar -Dant.home=$ANT_HOME org.apache.tools.ant.launch.Launcher $@
