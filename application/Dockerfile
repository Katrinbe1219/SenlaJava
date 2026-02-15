FROM tomcat:11-jdk21
RUN rm -rf /usr/local/tomcat/webapps/*
COPY target/senla.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
