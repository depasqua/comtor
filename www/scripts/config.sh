#!/bin/sh
# Define the directory of the COMTOR data area (where uploads are processed)
COMTOR_DATA=/mywebsite/comtor/comtor_data

# Define the directory of the COMTOR uploads
UPLOAD_PATH=$COMTOR_DATA/uploads

# Define the directory of the class files for comtor
CLASSES=$COMTOR_DATA/code/comtor.jar

# Define the CLASSPATH used to find all dependencies
# this CLASSPATH must include the locations of the following five files:
#	/comtor_data/code/stringtemplate-3.2.jar
#	/comtor_data/code/comtor.jar
#	/comtor_data/code/antlr-3.1.1.jar
#	/comtor_data/code/mysql-connector-java-5.1.6-bin.jar
#	/comtor_data/code/antlr-3.1.1-runtime.jar
# See below for an example.
CLASSPATH='.:$COMTOR_DATA/code/stringtemplate-3.2.jar:$COMTOR_DATA/code/comtor.jar:$COMTOR_DATA/code/antlr-3.1.1.jar:$COMTOR_DATA/code/mysql-connector-java-5.1.6-bin.jar:$COMTOR_DATA/code/antlr-3.1.1-runtime.jar'


# Define the directory of the java.properties file
JAVA_CONFIG=$COMTOR_DATA/config/java.properties


# where all of the following commands can be found
alias java='/usr/bin/java'
alias javac='/usr/bin/javac'
alias javadoc='/usr/bin/javadoc'
alias jar='/usr/bin/jar'
alias chmod='/bin/chmod'
alias mkdir='/bin/mkdir'
alias mv='/bin/mv'
alias rm='/bin/rm'
alias find='/usr/bin/find'
alias cat='/bin/cat'
alias grep='/bin/grep'
