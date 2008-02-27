#!/bin/sh
#set -x

PATH='/home/sigwart4/public_html'

#dir of class files for javadoc
CLASSES='/home/sigwart4/code/classes'

#create temp dir based on tempFileName
cd $PATH
/bin/mkdir $1
/bin/chmod 755 $1
cd $1

#create src dir for all source files to be stored within the temp dir
/bin/mkdir src
/bin/chmod 755 src

#move jar file from home dir to src
cd $PATH
/bin/mv $2 $1/src

#move text file (list of doclets) to the temp dir and rename it to Doclets.txt
/bin/mv $1.txt $1
cd $1
/bin/cp $1.txt Doclets.txt
/bin/rm $1.txt

#unjar the jar file in src dir and set the permissions
cd src
/etc/java/jdk1.6.0/bin/jar xf $2
cd ..
/bin/chmod 755 -R src

#move the jar file from the src dir back to the temp dir
cd src
/bin/mv $2 ../$2

#find the list of java files within the src dir and store the list in source.txt
cd $PATH/$1/
/usr/bin/find src -name *.java > source.txt
/bin/chmod 755 *.txt

#take the contents of source.txt and store it in MYVAR
MYVAR=`/bin/cat source.txt`

#make text file with userID
/bin/cat >> userID.txt << EOF
$3
EOF

#run javadoc
/etc/java/jdk1.6.0/bin/javadoc -doclet comtor.ComtorDriver -docletpath $CLASSES $MYVAR

#run ComtorReport.php
cd $PATH/$1
/usr/bin/php -f ComtorReport.php
