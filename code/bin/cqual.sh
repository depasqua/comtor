#!/bin/bash
#
# Copyright 2005 Michael Locasto
#
# $Id: cqual.sh,v 1.1.1.1 2006/07/10 16:22:02 locasto2 Exp $
#
# Eventually, like Valgrind, have a number of tools that measure each
# feature. Select between the tools here.
#
JAVA_HOME=/usr/java/jdk1.5.0_02
CQUAL_HOME=../
LIB_DIR="$CQUAL_HOME"/lib
RUNTIME_CLASSPATH="$SEMC_HOME"/classes/:
JAVADOC="$JAVA_HOME"/bin/javadoc

simple() {
    javadoc -classpath ../classes -doclet comtor.SimpleCommentQualityDoclet -docletpath ../classes ../src/comtor/examples/HelloWorld.java
   return 0
}

lengthfeature() {
    javadoc -classpath ../classes -doclet comtor.LengthFeatureDoclet -docletpath ../classes $@
    return 0
}

case "$1" in
    simple)
        simple
        ;;
    length)
        lengthfeature
        ;;
    *)
        echo $"Usage $0 {simple|length}"
        exit 1
esac

exit 0

