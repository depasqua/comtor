#!/bin/sh
#set -x

export set CLASSPATH=/usr/local/lib/mysql-connector-java-5.1.5-bin.jar:$CLASSPATH


PATH='/home/sigwart4/www'
UPLOAD_PATH='/home/sigwart4/www/upload'

#dir of class files for javadoc
CLASSES='/home/sigwart4/code/classes'

#create temp dir based on tempFileName
cd $UPLOAD_PATH
/bin/mkdir $1
/bin/chmod 755 $1
cd $1

#create src dir for all source files to be stored within the temp dir
/bin/mkdir src
/bin/chmod 755 src

#move jar file from upload dir to src
cd $UPLOAD_PATH
/bin/mv $2 $1/src

#move text file (list of doclets) to the temp dir and rename it to Doclets.txt
/bin/mv $1.txt $1
cd $1
/bin/cp $1.txt Doclets.txt
/bin/rm $1.txt

#unjar the jar file in src dir and set the permissions
cd src
/usr/bin/jar xf $2
cd ..
/bin/chmod 755 -R src

#move the jar file from the src dir back to the temp dir
cd src
/bin/mv $2 ../$2

#find the list of java files within the src dir and store the list in source.txt
cd $UPLOAD_PATH/$1/
/usr/bin/find src -name *.java > source.txt
/bin/chmod 755 *.txt

#compile and list class files
#cd src
#/usr/bin/javac *.java
#/usr/bin/find *.class > classes.txt
#cd ..

#take the contents of source.txt and store it in MYVAR
MYVAR=`/bin/cat source.txt`

#make text file with userId
/bin/cat >> userId.txt << EOF
$3
EOF

#run javadoc
/usr/bin/javadoc -doclet comtor.ComtorDriver -docletpath $CLASSES $MYVAR

# Delete the temporary folder
/bin/rm -r $UPLOAD_PATH/$1
