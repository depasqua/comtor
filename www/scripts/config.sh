#!/bin/sh

#dir of upload path
UPLOAD_PATH=/mywebsite/comtor/comtor_data/uploads

#dir of class files for javadoc
CLASSES=/mywebsite/comtor/comtor_data/code/comtor.jar

#classpath used to find all dependencies
#this classpath must include the locations of the following five files:
#	/comtor_data/code/stringtemplate-3.2.jar
#	/comtor_data/code/comtor.jar
#	/comtor_data/code/antlr-3.1.1.jar
#	/comtor_data/code/mysql-connector-java-5.1.6-bin.jar
#	/comtor_data/code/antlr-3.1.1-runtime.jar
#See below for an example.
CLASSPATH='.:/mywebsite/comtor/comtor_data/code/stringtemplate-3.2.jar:/mywebsite/comtor/comtor_data/code/comtor.jar:/mywebsite/comtor/comtor_data/code/antlr-3.1.1.jar:/mywebsite/comtor/comtor_data/code/mysql-connector-java-5.1.6-bin.jar:/mywebsite/comtor/comtor_data/code/antlr-3.1.1-runtime.jar'

#dir of the java.properties file
JAVA_CONFIG=/mywebsite/comtor/comtor_data/config/java.properties

#where all of the following commands can be found
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
 

?>
