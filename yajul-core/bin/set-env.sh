#!/bin/sh
YAJUL_HOME=.
ANT_HOME=/share/app/ant/1.5.1
JAVA_HOME=/usr/j2se/1.3.1.04
PATH=$JAVA_HOME/bin:$ANT_HOME/bin:$PATH
export ANT_HOME JAVA_HOME PATH
# Add all jars in the lib directory to the classpath.
for i in "$YAJUL_HOME"/lib/*.jar
do
  # if the directory is empty, then it will return the input string
  # this is stupid, so case for it
  if [ -f "$i" ] ; then
    if [ -z "$LOCALCLASSPATH" ] ; then
      LOCALCLASSPATH="$i"
    else
      LOCALCLASSPATH="$i":"$LOCALCLASSPATH"
    fi
  fi
done
CLASSPATH=$LOCALCLASSPATH:$CLASSPATH
export CLASSPATH
