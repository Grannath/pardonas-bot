FROM openjdk:8
VOLUME /tmp
ADD build/libs/pardonas-bot-1.0.0.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-Xmx1024m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]