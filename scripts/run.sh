#!/bin/bash
set -e
set -u
java -classpath bin:resources:lib/log4j-1.2.15.jar:lib/commons-lang-2.4.jar net.abstractplain.tiletown.TileTownMain
