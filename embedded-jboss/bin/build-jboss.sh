#!/bin/bash
JBOSSAS_BASE=~/svn/jbossas
JBOSSAS_CHECKOUT=$JBOSSAS_BASE/branches/Branch_5_x
EMB_CHECKOUT=$JBOSSAS_BASE/projects/embedded/trunk

JAVA_HOME=/opt/jdk1.6.0_12
export JAVA_HOME
M2_HOME=/opt/maven
export M2_HOME

echo "Building JBoss AS..."

cd $JBOSSAS_CHECKOUT
$M2_HOME/bin/mvn clean install

cd build
./build.sh

echo "Building Embedded..."

cd $EMB_CHECKOUT
$M2_HOME/bin/mvn clean install


# cd testsuite-fulldep
# export JBOSS_HOME=$JBOSSAS_CHECKOUT/build/output

