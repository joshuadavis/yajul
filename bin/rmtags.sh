#!/bin/bash
# rmtags.sh - Removes git tags remotely and locally

function die() {
	echo "ERROR: $1"
	exit 1
}

LOCAL_CMD="git tag -d "
REMOTE_CMD="git push origin "
for x in $*
do
	LOCAL_CMD="$LOCAL_CMD $x"
	REMOTE_CMD="$REMOTE_CMD :refs/tags/$x"	
done

echo "Removing local tags with $LOCAL_CMD..."
$LOCAL_CMD || die "$LOCAL_CMD failed!"

echo "Removing remote tags with $REMOTE_CMD..."
$REMOTE_CMD || die "$REMOTE_CMD failed!"


