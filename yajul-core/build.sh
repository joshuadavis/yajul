#!/bin/sh
java -cp "../tools/ant/lib/ant-launcher.jar;$JAVA_HOME/lib/tools.jar" -Dant.home=../tools/ant org.apache.tools.ant.launch.Launcher $@
