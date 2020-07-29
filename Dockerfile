FROM java:8-jdk-alpine
LABEL maintainer="elias_meyer@yahoo.com.br"
VOLUME /tmp
COPY target/compasso-votemanager-api-*.jar compasso-votemanager-api.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "compasso-votemanager-api.jar"]
EXPOSE 8080
