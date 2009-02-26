#!/bin/sh
#set -x

# Script expects to be run from www folder

# Load config info
. scripts/config.sh

export set CLASSPATH=$CLASSPATH

#create temp dir based on tempFileName
cd $UPLOAD_PATH
#/bin/mkdir $1
/bin/chmod 755 $1
cd $1

#create src dir for all source files to be stored within the temp dir
/bin/mkdir src
/bin/chmod 755 src

#move jar file from upload dir to src
/bin/mv $2 src

#move text file (list of doclets) to the temp dir and rename it to Doclets.txt
cd $UPLOAD_PATH
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

# Check that there is source code
if [ ! -s "./source.txt" ]; then
  # Delete the temporary folder
  /bin/rm -r $UPLOAD_PATH/$1
  exit 3
fi

#compile and list class files
cd src
/usr/bin/javac `/usr/bin/find . | /bin/grep .*\.java$` > ../CompileOut.txt 2>&1
# Get return value of javac
compiled=$?
/usr/bin/find . | /bin/grep .*\.class$ > classes.txt
cd ..

#take the contents of source.txt and store it in MYVAR
MYVAR=`/bin/cat source.txt`

#make text file with userId
/bin/cat >> userId.txt << EOF
$3
EOF

# Run javadoc if code compiled
if [ $compiled = 0 ]; then
  /usr/bin/javadoc -private --assignment-id $4 --config-file $JAVA_CONFIG -doclet comtor.ComtorDriver -docletpath $CLASSES $MYVAR > JavadocOut.txt 2>&1
  javadocRtn=$?
else
  JAVA_PATH -cp $CLASSES:$CLASSPATH comtor.GenerateErrorReport $JAVA_CONFIG
fi

if [ $javadocRtn != 0 ]; then
exit 2
fi

# Delete the temporary folder
/bin/rm -r $UPLOAD_PATH/$1

if [ $compiled != 0 ]; then
exit 1
fi

exit 0
