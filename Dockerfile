FROM openjdk:8-jre-alpine
RUN mkdir /app
RUN cd /app
COPY target/*.jar /app/receiver-app.jar
WORKDIR /app
EXPOSE 9000
CMD exec java -jar "receiver-app.jar"
