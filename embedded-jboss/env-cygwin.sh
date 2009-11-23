#!/bin/bash
export JAVA_HOME=/opt/jdk1.6.0_12
export M2_HOME=/opt/apache-maven-2.0.10
export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH
export JBOSS_HOME=`cygpath -w /home/josh/embedded-jboss/jboss-as/build/output/jboss-6.0.0.M1`
