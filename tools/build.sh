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
ANT_CLASSPATH="-classpath $ANT_HOME/lib/ant-launcher.jar"
ANT_OPTS="-Dant.home=$ANT_HOME -Djava.awt.headless=true"
java $ANT_CLASSPATH $ANT_OPTS org.apache.tools.ant.launch.Launcher $@
