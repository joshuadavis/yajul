#!/bin/sh
# yajul-build.sh - This script runs the nightly build using the following arguments:
# $1 - The build directory
# $2 - The CVS username
function usage()
{
   echo "usage: $0 builddir cvsusername"
   echo "    builddir    - The name of the directory to use for the build."
   echo "    cvsusername - The CVS username"     
   exit -1
}

if [ -z $1 ]; then
    echo "Required argument 'builddir' was not specified."
    useage()
fi
if [ -z $2 ]; then
    echo "Required argument 'cvsusername' was not specified."
    useage()
fi
export BUILD_DIR=$1
export CVSROOT=:ext:$2@cvs.sourceforge.net:/cvsroot/yajul
export CVS_RSH=ssh
export JAVA_HOME=/usr/java/j2sdk1.4.2_03
if [ -d $BUILD_DIR ]
then mkdir -p $BUILD_DIR
fi
cd $BUILD_DIR
echo "Updating $BUILD_DIR/tools ..."
cvs -z3 co tools
cd tools
./build.sh
