#!/bin/sh
rsync -avz -e ssh ../yajul-core/build/docs/api pgmjsd@yajul.sourceforge.net:/home/groups/y/ya/yajul/htdocs/yajul-core
