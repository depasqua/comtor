#!/bin/bash
#
# Copyright 2006 Michael Locasto
#
# $Id$
#
JAVA_HOME=/usr/java/jdk1.5.0_02
CQUAL_HOME=../
LIB_DIR="$CQUAL_HOME"/lib
RUNTIME_CLASSPATH="$SEMC_HOME"/classes/:
JAVADOC="$JAVA_HOME"/bin/javadoc

repmes() {
    opt_in_disclaimer="\nYou have chosen to report the results of your comment quality measurement.\nThis report contains personally identifiable information. The information\ncollected is being used strictly to support the research of the comment \nquality tool itself, and your personal information (your Unix user ID)\nwill not be used in any other way, nor will the rating you submit have\nany impact on your grade.";
    echo -e $opt_in_disclaimer;
    echo "";
    read -p "Do you accept the terms of use? (Y/n) " myanswer;
    echo $myanswer;
    mydate=`date +%s`;
    mykernel=`uname -r`;
    echo "date=  " $mydate;
    echo "kernel=" $mykernel;
    echo "USER=  " $USER;
    echo "PID=   " $PID;
    echo "PID-d= " $$;
    echo "PWD=   " $PWD;
    echo "CWD=   " $CWD;
    echo "args=   " $@;
}

simple() {
    javadoc -classpath ../classes -doclet comtor.SimpleCommentQualityDoclet -docletpath ../classes ../src/comtor/examples/HelloWorld.java
   return 0
}

cflow() {
    javadoc -classpath ../classes -doclet comtor.ControlFlowDoclet -docletpath ../classes ../src/comtor/examples/CntrlFlow.java
}

lengthfeature() {
    javadoc -classpath ../classes -doclet comtor.LengthFeatureDoclet -docletpath ../classes $@
    return 0
}

case "$1" in
    --report-measurement)
        repmes
        ;;
    -r)
        repmes
        ;;
    simple)
        simple
        ;;
    cflow)
        cflow
        ;;
    length)
        lengthfeature
        ;;
    *)
        echo $"Usage $0 {--report-measurement|-r|simple|length|cflow}"
        exit 1
esac

exit 0

