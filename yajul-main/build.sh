#!/bin/sh
# ----- Verify and Set Required Environment Variables -------------------------

if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

if [ "$CENTIPEDE_HOME" = "" ] ; then
  echo You must set CENTIPEDE_HOME to point at your Centipede installation
  exit 1
fi

# ----- Verify and Set Required Environment Variables -------------------------

if [ "$TERM" = "cygwin" ] ; then
  S=';'
else
  S=':'
fi

$CENTIPEDE_HOME/bin/cent