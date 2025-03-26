#java 17 Base image
FROM eclipse-temurin:17-jdk
#workdir
WORKDIR /app
#COPY jar file
COPY build/libs/rion-0.0.1-SNAPSHOT.jar rion-0.0.1-SNAPSHOT.jar
#metadata
LABEL authors="kyumin"
#edit configurations
ENV AWS_ACCESS_KEY_ID={비밀}
ENV AWS_SECRET_ACCESS_KEY={비밀}
#PORTNUM
EXPOSE 5000
#CMD
CMD ["java", "-jar", "rion-0.0.1-SNAPSHOT.jar"]
