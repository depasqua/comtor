#!bin/bash
#
#######################################################################
#                                                                     #
#Note on usage:                                                       #
#                                                                     #
#This file will call an ant build file to compile the COMTOR source   #
#code.                                                                #
#                                                                     #
#######################################################################

echo "Compiling COMTOR code files..."

cd code/
mkdir classes

export set CLASSPATH=.:$CLASSPATH:.:/home/james/public_html/COMTOR_3/comtor/comtor_data/code/stringtemplate-3.2.jar:/home/james/public_html/COMTOR_3/comtor/comtor_data/code/antlr-3.1.1.jar:/home/james/public_html/COMTOR_3/comtor/comtor_data/code/mysql-connector-java-5.1.6-bin.jar:/home/james/public_html/COMTOR_3/comtor/comtor_data/code/comtor.jar:/home/james/public_html/COMTOR_3/comtor/comtor_data/code/antlr-3.1.1-runtime.jar

echo "Now compiling..."

ant compile

echo "Done."

cd classes
jar -cvf comtor.jar comtor

exit $?

