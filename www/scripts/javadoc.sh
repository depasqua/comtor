#!/bin/sh
#set -x

# Script expects to be run from www folder

# Load config info
. scripts/config.sh

export set CLASSPATH=$CLASSPATH

#create temp dir based on tempFileName
cd $UPLOAD_PATH
#mkdir $1
chmod 755 $1
cd $1

#create src dir for all source files to be stored within the temp dir
mkdir src
chmod 755 src

#move jar file from upload dir to src
mv $2 src

#move text file (list of doclets) to the temp dir and rename it to Doclets.txt
cd $UPLOAD_PATH
mv $1.txt $1
cd $1
cp $1.txt Doclets.txt
rm $1.txt

#unjar the jar file in src dir and set the permissions
cd src
jar xf $2
cd ..
chmod 755 -R src

#move the jar file from the src dir back to the temp dir
cd src
mv $2 ../$2

#find the list of java files within the src dir and store the list in source.txt
cd $UPLOAD_PATH/$1/
find src -name *.java > source.txt
chmod 755 *.txt

# Check that there is source code
if [ ! -s "./source.txt" ]; then
  # Delete the temporary folder
  rm -r $UPLOAD_PATH/$1
  exit 3
fi

#compile and list class files
cd src
javac `find . | grep .*\.java$` > ../CompileOut.txt 2>&1
# Get return value of javac
compiled=$?
find . | grep .*\.class$ > classes.txt
cd ..

#take the contents of source.txt and store it in MYVAR
MYVAR=`cat source.txt`

#make text file with userId
cat >> userId.txt << EOF
$3
EOF

# Run javadoc if code compiled
if [ $compiled = 0 ]; then
  javadoc -private --assignment-id $4 --config-file $JAVA_CONFIG -doclet comtor.ComtorDriver -docletpath $CLASSES $MYVAR > JavadocOut.txt 2>&1
  javadocRtn=$?
else
  java -cp $CLASSES:$CLASSPATH comtor.GenerateErrorReport $JAVA_CONFIG
fi

if [ $javadocRtn != 0 ]; then
exit 2
fi

# Delete the temporary folder
rm -r $UPLOAD_PATH/$1

if [ $compiled != 0 ]; then
exit 1
fi

exit 0
