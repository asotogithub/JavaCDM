FROM tomcat:7
Maintainer TE Galaxy Team <galaxy@trueffect.com>

RUN rm -rf /usr/local/tomcat/webapps/ROOT*

ADD deploy/tomcat/start.sh /usr/local/tomcat/start.sh
RUN chmod +x /usr/local/tomcat/start.sh

# Copy the project
ADD public/target/public-*.war /usr/local/tomcat/webapps/cms.war
ADD oauth/target/oauth-*.war /usr/local/tomcat/webapps/oauth.war
ADD tpasapi/target/tpasapi-*.war /usr/local/tomcat/webapps/3pasapi.war

ENV TZ="MST"
ENV CATALINA_OPTS="-Xms512M -Xmx2048M"

# Run as the prodman UID
RUN chown -R 10001:31 /usr/local/tomcat
USER 10001:31

CMD /usr/local/tomcat/start.sh
