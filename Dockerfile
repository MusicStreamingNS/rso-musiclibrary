FROM openjdk:8-jre-slim

RUN mkdir /app

WORKDIR /app

ADD ./api/target/musiclibrary-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD java -jar musiclibrary-api-1.0.0-SNAPSHOT.jar