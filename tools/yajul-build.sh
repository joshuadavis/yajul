#!/bin/sh
# yajul-build.sh - This script runs the nightly build using the following arguments:
# $1 - The build directory
# $2 - The CVS username
function usage {
   echo "usage: $0 builddir cvsusername"
   echo "    builddir    - The name of the directory to use for the build."
   echo "    cvsusername - The CVS username"     
   exit -1
}

if [ -z $1 ]; then
    echo "Required argument 'builddir' was not specified."
    usage
fi
if [ -z $2 ]; then
    echo "Required argument 'cvsusername' was not specified."
    usage
fi
export BUILD_DIR=$1
export CVSROOT=:ext:$2@cvs.sourceforge.net:/cvsroot/yajul
export CVS_RSH=ssh
export JAVA_HOME=/usr/java/j2sdk1.4.2_03
if [ ! -d $BUILD_DIR ]; then
    mkdir -p $BUILD_DIR
    echo "Created $BUILD_DIR"
fi
if [ ! -d $BUILD_DIR ]; then
    echo "$0: Directory $BUILD_DIR could not be created!";
    exit -2;
fi
# Bootstrap by downloading the 'tools' module from CVS.
cd $BUILD_DIR
TOOLS_DIR="$BUILD_DIR/tools"
echo "Updating $TOOLS_DIR ..."
cvs -z9 -q co tools
if [ ! -d $TOOLS_DIR ]; then
    echo "$0: Directory $TOOLS_DIR does not exist!";
    exit -3;
fi
cd $TOOLS_DIR
echo "Running build.sh  in `pwd` ..."
./build.sh