FROM adoptopenjdk:15-jre-hotspot

RUN mkdir /app

WORKDIR /app

ADD ./api/target/scheduler-api-1.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "scheduler-api-1.0-SNAPSHOT.jar"]
#ENTRYPOINT ["java", "-jar", "scheduler-api-1.0-SNAPSHOT.jar"]
#CMD java -jar scheduler-api-1.0-SNAPSHOT.jar