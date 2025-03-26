#java 17 Base image
FROM eclipse-temurin:17-jdk
#workdir
WORKDIR /app
#COPY jar file
COPY build/libs/rion-0.0.1-SNAPSHOT.jar rion-0.0.1-SNAPSHOT.jar
#metadata
LABEL authors="kyumin"
#edit configurations
ENV AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
ENV AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
#PORTNUM
EXPOSE 5000
#CMD
CMD ["java", "-jar", "rion-0.0.1-SNAPSHOT.jar"]
