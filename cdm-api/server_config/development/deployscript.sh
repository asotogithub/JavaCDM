#!/bin/bash
echo "Removing war files";
rm -f /opt/trueffect/truapi/apache-tomcat/webapps/oauth#v1.0b1.war;
rm -f /opt/trueffect/truapi/apache-tomcat/webapps/cms#v1.0b1.war;
rm -f /opt/trueffect/truapi/apache-tomcat/webapps/3pasapi#v1.0b1.war;

while [ -d "/opt/trueffect/truapi/apache-tomcat/webapps/oauth#v1.0b1" ]
do
    sleep 1;
    echo "Waiting for the server..."
done
cp /opt/trueffect/truapi/apache-tomcat/wars/builds/development/oauth-dev.xml /opt/trueffect/truapi/apache-tomcat/conf/Catalina/localhost/oauth#v1.0b1.xml;
cp /opt/trueffect/truapi/apache-tomcat/wars/builds/oauth-v1.0.0-*.war /opt/trueffect/truapi/apache-tomcat/webapps/oauth#v1.0b1.war;

while [  -d "/opt/trueffect/truapi/apache-tomcat/webapps/cms#v1.0b1" ]
do
    sleep 1;
    echo "Waiting for the server..."
done
cp /opt/trueffect/truapi/apache-tomcat/wars/builds/development/cms-dev.xml /opt/trueffect/truapi/apache-tomcat/conf/Catalina/localhost/cms#v1.0b1.xml;
cp /opt/trueffect/truapi/apache-tomcat/wars/builds/public-v1.0.0-*.war /opt/trueffect/truapi/apache-tomcat/webapps/cms#v1.0b1.war;

while [ -d "/opt/trueffect/truapi/apache-tomcat/webapps/3pasapi#v1.0b1" ]
do
    sleep 1;
    echo "Waiting for the server..."
done
cp /opt/trueffect/truapi/apache-tomcat/wars/builds/development/3pasapi-dev.xml /opt/trueffect/truapi/apache-tomcat/conf/Catalina/localhost/3pasapi#v1.0b1.xml;
cp /opt/trueffect/truapi/apache-tomcat/wars/builds/tpasapi-v1.0.0-*.war /opt/trueffect/truapi/apache-tomcat/webapps/3pasapi#v1.0b1.war;

touch /opt/trueffect/truapi/apache-tomcat/conf/Catalina/localhost/oauth#v1.0b1.xml;
touch /opt/trueffect/truapi/apache-tomcat/conf/Catalina/localhost/cms#v1.0b1.xml;
touch /opt/trueffect/truapi/apache-tomcat/conf/Catalina/localhost/3pasapi#v1.0b1.xml;

echo "Deploying Fitnesse ............................"
rm -rf /opt/trueffect/fitnesse/FitNesseRoot/FrontPage;
rm -rf /opt/trueffect/fitnesse/FitNesseRoot/Trueffect*;
rm -rf /opt/trueffect/fitnesse/FitNesseRoot/Feature*;
cp -r /opt/trueffect/truapi/apache-tomcat/wars/builds/fitnesse/FrontPage /opt/trueffect/fitnesse/FitNesseRoot/;
cp -r /opt/trueffect/truapi/apache-tomcat/wars/builds/fitnesse/Trueffect* /opt/trueffect/fitnesse/FitNesseRoot/;
rm -rf /opt/trueffect/fitnesse/lib;
cp -r /opt/trueffect/truapi/apache-tomcat/wars/builds/lib /opt/trueffect/fitnesse/;

rm -rf /opt/trueffect/fitnesse/resources;
cp -r /opt/trueffect/truapi/apache-tomcat/wars/builds/fitnesse/resources /opt/trueffect/fitnesse/