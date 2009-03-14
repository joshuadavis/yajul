#!/bin/sh
ACCOUNT=$1
rsync --rsh=ssh -Cavz target/site/ $ACCOUNT@shell.sourceforge.net:./htdocs/
