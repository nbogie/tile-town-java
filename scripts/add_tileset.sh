#!/bin/bash
set -e
set -u

src_dir=$1
dest_dir=$2

mkdir -p $dest_dir
echo "made dest dir: $dest_dir"

cp -R $src_dir/* $dest_dir

