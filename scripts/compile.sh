#!/usr/bin/env bash

set -e
set -u
dest_dir=bin
resources_dir=resources
rm -rf $dest_dir
mkdir -p $dest_dir
cp -R $resources_dir/* $dest_dir
echo "Copied all resources from $resources_dir into newly blanked $dest_dir"
echo "Compiling..."
javac -d $dest_dir -classpath src:lib/log4j-1.2.15.jar:lib/commons-lang-2.4.jar:lib/junit-4.8.1.jar src/net/abstractplain/tiletown/*.java
echo "Compiled into $dest_dir"
