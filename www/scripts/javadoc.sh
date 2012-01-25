#!/bin/sh
#
# Parameters to this script:
# $1: temporary directory, previously created by run.php, where processing will be handled
# $2: name of the uploaded jar file
# $3: user id
# $4: assignment id
#
set -u

# Script expects to be run from www folder

# Load config info
. scripts/config.sh
export set CLASSPATH=$CLASSPATH

# change the permissions on the temporary directory ($1) and doclet list file ($1.txt)
chmod 755 $UPLOAD_PATH/$1
cd $UPLOAD_PATH/$1
mv $UPLOAD_PATH/$1.txt ./docletList.properties

# unjar the jar file in temp dir and set the permissions
jar xf $2

# find the list of java files within the src dir and store the list in source.txt
find . -name *.java > source.txt

# Check that there is source code
if [ ! -s "source.txt" ]; then
  # Delete the temporary folder
  rm -r $UPLOAD_PATH/$1
  exit 3
fi

# compile and list class files
javac `find . | grep .*\.java$` > ../compileOut.txt 2>&1

# obtain return value of javac
compiled=$?

# obtain the contents of source.txt and store it in MYVAR
sourceFiles=`cat source.txt`

# make text file with userId
cat >> $UPLOAD_PATH/$1/userId.txt << EOF
$3
EOF

# Run javadoc if code compiled
if [ $compiled = 0 ]; then
  javadoc -private --assignment-id $4 --config-file $JAVA_CONFIG -doclet org.comtor.drivers.ComtorDriver -docletpath $CLASSES $sourceFiles > javadocOut.txt 2>&1
  echo "javadoc -private --assignment-id $4 --config-file $JAVA_CONFIG -doclet org.comtor.drivers.ComtorDriver -docletpath $CLASSES $sourceFiles" > $UPLOAD_PATH/$1/debug.txt
  javadocRtn=$?
else
  java -cp $CLASSES:$CLASSPATH org.comtor.reporting.GenerateErrorReport $JAVA_CONFIG
  echo "java -cp $CLASSES:$CLASSPATH org.comtor.reporting.GenerateErrorReport $JAVA_CONFIG" >> $UPLOAD_PATH/$1/debug.txt
fi

if [ $javadocRtn != 0 ]; then
	exit 2
fi

# Delete the temporary folder
#rm -r $UPLOAD_PATH/$1

if [ $compiled != 0 ]; then
	exit 1
fi

exit 0
