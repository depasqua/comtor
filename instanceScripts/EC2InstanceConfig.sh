#!/bin/bash

# Update the current packages and install required packages for COMTOR's use.
apt-get update -y
apt-get upgrade -y
apt-get install -y zip unzip tree tomcat7 tomcat7-admin openjdk-7-jdk libcommons-codec-java libcommons-fileupload-java libcommons-io-java libcommons-logging-java libhttpcore-java libhttpclient-java libjackson-json-java

# Link in required jar files for use in Tomcat server
cd /usr/share/tomcat7/lib
ln -s ../../java/httpclient.jar
ln -s ../../java/httpcore.jar
ln -s ../../java/commons-codec.jar
ln -s ../../java/commons-fileupload.jar
ln -s ../../java/commons-io.jar
ln -s ../../java/commons-logging.jar
ln -s ../../java/jackson-core.jar
ln -s ../../java/jackson-core-asl.jar
ln -s /usr/lib/jvm/java-7-openjdk-i386/lib/tools.jar # not guaranteed to be i386, check for amd

# Download and install LibJackson json libraries
wget http://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.4.1/jackson-core-2.4.1.jar
wget http://repo2.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.4.0/jackson-annotations-2.4.0.jar
wget http://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.4.1/jackson-databind-2.4.1.jar

# Download and install the CORS library and required java properties utilities library
wget --no-check-certificate https://bitbucket.org/thetransactioncompany/cors-filter/downloads/cors-filter-1.9.2.jar
wget -O java-properties-util-1.9.jar http://search.maven.org/remotecontent?filepath=com/thetransactioncompany/java-property-utils/1.9/java-property-utils-1.9.jar

# Download the java mail jar, place it in /usr/share/java and link it to the tomcat lib
cd /usr/share/java
wget http://java.net/projects/javamail/downloads/download/javax.mail.jar
cd /usr/share/tomcat7/lib
ln -s ../../java/javax.mail.jar

# Download and install the reCAPTCHA library
wget --no-check-certificate -O recaptcha4j-0.0.7.zip https://recaptcha.googlecode.com/files/recaptcha4j-0.0.7.zip
unzip recaptcha4j-0.0.7.zip recaptcha4j-0.0.7/recaptcha4j-0.0.7.jar
mv recaptcha4j-0.0.7/*.jar .
rm -rf recaptcha4j-0.0.7/
rm recaptcha4j-0.0.7.zip

# Download and install the AWS Java API
wget http://sdk-for-java.amazonwebservices.com/latest/aws-java-sdk.zip
unzip aws-java-sdk.zip aws-java-sdk-1.8.0/lib/aws-java-sdk-1.8.0.jar
unzip aws-java-sdk.zip aws-java-sdk-1.8.0/third-party/joda-time-2.2/joda-time-2.2.jar
mv aws-java-sdk-1.8.0/lib/aws-java-sdk-1.8.0.jar .
mv aws-java-sdk-1.8.0/third-party/joda-time-2.2/joda-time-2.2.jar .
rm -rf aws-java-sdk-1.8.0
rm aws-java-sdk.zip

# Download and install Apache Log4J2
wget http://www.apache.org/dist/logging/log4j/2.0-rc1/apache-log4j-2.0-rc1-bin.zip
unzip apache-log4j-2.0-rc1-bin.zip apache-log4j-2.0-rc1-bin/log4j-api-2.0-rc1.jar
unzip apache-log4j-2.0-rc1-bin.zip apache-log4j-2.0-rc1-bin/log4j-core-2.0-rc1.jar
mv apache-log4j-2.0-rc1-bin/*.jar .
rm -rf apache-log4j-2.0-rc1-bin/
rm apache-log4j-2.0-rc1-bin.zip

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
