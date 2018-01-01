FROM java:8-jre-alpine
VOLUME /tmp

ADD *.jar app.jar
# RUN bash -c 'touch /app.jar'
EXPOSE 10003

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/.urandom","-jar","/app.jar"]