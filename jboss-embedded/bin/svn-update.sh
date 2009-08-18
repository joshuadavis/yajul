#!/bin/bash
JBOSSAS_BASE=~/svn/jboss-embedded
JBOSSAS_CHECKOUT=$JBOSSAS_BASE/Branch_5_x
EMB_CHECKOUT=$JBOSSAS_BASE/embedded/trunk

cd $JBOSSAS_CHECKOUT
svn up .

cd $EMB_CHECKOUT
svn up .

# cd testsuite-fulldep
# export JBOSS_HOME=$JBOSSAS_CHECKOUT/build/output

