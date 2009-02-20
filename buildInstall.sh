#!/bin/bash
# Important Notes:
#   - Be sure to put this in an empty directory before running!
#   - To get a specific tagged version, add the version name as the first parameter
#   - Make sure to update both the schema.sql and data.sql files in comtor/mysqlSchema 
#   - The created installer.tar.gz needs to be manually added to CVS
# Other Notes:
#   - If there are additional resources from the last installer, you need to decompress the installer, move the resources to comtor_data/resources, and recompress the files
#   - If the config files change, you need to decompress the installer, change the installer php code, and recompress the files

# Get a copy of the needed files from CVS
if [ $1 ]; then
  cvs -z3 -d:pserver:anonymous@comtor.cvs.sourceforge.net:/cvsroot/comtor export -r $1 comtor/web comtor/code comtor/mysqlSchema comtor/install/installer.tar.gz
else
  cvs -z3 -d:pserver:anonymous@comtor.cvs.sourceforge.net:/cvsroot/comtor export -D NOW comtor/web comtor/code comtor/mysqlSchema comtor/install/installer.tar.gz
fi

# Decompress existing installer
cd comtor/install
gzip -d installer.tar.gz
tar -xf installer.tar
rm installer.tar
cd ../..

# Compile the code
cd comtor/code
export set CLASSPATH=.:$CLASSPATH:/usr/local/lib/mysql-connector-java-5.1.6-bin.jar:/usr/share/java/antlr-2.7.7.jar:/usr/share/java/antlr-3.1.1.jar:/usr/share/java/antlr-3.1.1-runtime.jar:/usr/share/java/stringtemplate-3.2.jar  
ant compile
cd classes
jar -cvf comtor.jar comtor
cd ../../..

# Make installer folder
mkdir installer
cd installer

# Move the www folder 
mkdir www
mv ../comtor/web/* www

# Move the comtor_data folder
mv ../comtor/install/comtor_data/ .

# Move the newly compiled code
mv ../comtor/code/classes/comtor.jar comtor_data/code/

# Move the install html folder
mv ../comtor/install/install/ install

# Copy schema data
mv ../comtor/mysqlSchema/schema.sql install/
mv ../comtor/mysqlSchema/data.sql install/

# Tar and gzip the installer 
tar -cf installer.tar comtor_data/ install/ www/
gzip installer.tar

# Move installer and delete all other files
mv installer.tar.gz ..
cd ..
rm -r comtor/ installer/

