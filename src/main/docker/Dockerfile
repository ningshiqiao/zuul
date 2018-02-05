FROM java:8-jre-alpine
VOLUME /tmp

ADD *.jar app.jar
# RUN bash -c 'touch /app.jar'
RUN ln -sf /usr/share/zoneinfo/Asia/Jakarta  /etc/localtime
EXPOSE 10003

ENTRYPOINT ["java","-Xmx1024m","-Djava.security.egd=file:/dev/.urandom","-jar","/app.jar"]