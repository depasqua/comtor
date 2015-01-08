#!/bin/bash

# Update the current packages and install required packages for COMTOR's use.
apt-get update -y
apt-get upgrade -y
apt-get install -y zip unzip tree tomcat7 tomcat7-admin openjdk-7-jdk libcommons-codec-java libcommons-fileupload-java libcommons-io-java libcommons-logging-java libhttpcore-java libhttpclient-java libjackson-json-java

# Download the COMTOR library bundle from AWS bucket, place in a safe location
mkdir /tmp/comtor
cd /tmp/comtor
wget --no-verbose --no-check-certificate https://s3.amazonaws.com/org.comtor/comtorArchive.zip
unzip comtorArchive.zip
wget --no-verbose --no-check-certificate https://s3.amazonaws.com/org.comtor/comtorcore.jar

# Link in required jar files for use in Tomcat server
cd /usr/share/tomcat7/lib
ln -s ../../java/commons-codec.jar
ln -s ../../java/commons-fileupload.jar
ln -s ../../java/commons-io.jar
ln -s ../../java/commons-logging.jar
ln -s ../../java/httpclient.jar
ln -s ../../java/httpcore.jar
ln -s ../../java/jackson-core-lgpl.jar
ln -s ../../java/jackson-core-asl.jar
ln -s /usr/lib/jvm/java-7-openjdk-amd64/lib/tools.jar
mv /tmp/comtor/comtorcore.jar .

# Download and install LibJackson json libraries
mv /tmp/comtor/jackson-core-2.4.1.jar .
mv /tmp/comtor/jackson-annotations-2.4.0.jar .
mv /tmp/comtor/jackson-databind-2.4.1.jar .

# Download and install the CORS library and required java properties utilities library
mv /tmp/comtor/cors-filter-2.2.1.jar .
mv /tmp/comtor/java-properties-util-1.9.1.jar .

# Download the java mail jar, place it in /usr/share/java and link it to the tomcat lib
cd /usr/share/java
mv /tmp/comtor/javax.mail.jar .
cd /usr/share/tomcat7/lib
ln -s ../../java/javax.mail.jar

# Download and install the reCAPTCHA library
mv /tmp/comtor/recaptcha4j-0.0.7.zip .
unzip recaptcha4j-0.0.7.zip recaptcha4j-0.0.7/recaptcha4j-0.0.7.jar
mv recaptcha4j-0.0.7/*.jar .
rm -rf recaptcha4j-0.0.7/
rm recaptcha4j-0.0.7.zip

# Download and install the AWS Java API
mv /tmp/comtor/aws-java-sdk.zip .

##### VERSION NUMBER PROBLEM HERE!!!!
unzip aws-java-sdk.zip aws-java-sdk-*/lib/aws-java-sdk-[0-9.]*[0-9].jar
unzip aws-java-sdk.zip aws-java-sdk-*/third-party/joda-time-[0-9.]*[0-9]/joda-time-[0-9.]*[0-9].jar
mv aws-java-sdk-*/lib/aws-java-sdk-*.jar .
mv aws-java-sdk-*/third-party/joda-time-*/joda-time-*.jar .
rm -rf aws-java-sdk-[0-9.]*[0-9]
rm aws-java-sdk.zip

# Download and install Apache Log4J2

### THIS IS OLD
wget --no-verbose http://www.apache.org/dist/logging/log4j/2.1/apache-log4j-2.1-bin.zip
unzip apache-log4j-2.1-bin.zip apache-log4j-2.1-bin/log4j-api-2.1.jar
unzip apache-log4j-2.1-bin.zip apache-log4j-2.1-bin/log4j-core-2.1.jar
mv apache-log4j-2.1-bin/*.jar .
rm -rf apache-log4j-2.1-bin/
rm apache-log4j-2.1-bin.zip

# Stop the Tomcat service and remove the ROOT web application
# (we'll need to deploy the COMTOR application as root)
service tomcat7 stop
cd /var/lib/tomcat7/webapps/
mkdir ../webapps.old
mv ROOT ../webapps.old/ROOT.sav

# Change the Tomcat port from 8080 to 80
cd /var/lib/tomcat7/conf/
sed -i 's/\( *<Connector port=\)"8080"\(.\+\)/\1"80"\2/' server.xml

# Change the AUTHBIND setting for Tomcat
cd /etc/default
sed -i 's/^#\(AUTHBIND=\)no/\1yes/' tomcat7

# Enable the binding to port 80 for Tomcat7
sudo touch /etc/authbind/byport/80
sudo chmod 500 /etc/authbind/byport/80
sudo chown tomcat7 /etc/authbind/byport/80

service tomcat7 start
