#FROM adoptopenjdk/openjdk11:alpine-jre
FROM openjdk:11
WORKDIR /opt/app
RUN apt-get -y update
RUN apt-get -y install git
RUN git clone https://github.com/nanstar2020/AWSConnect.git
WORKDIR /opt/app/AWSConnect
RUN chmod a+x mvnw
RUN ./mvnw package
ENTRYPOINT ["java", "-jar", "target/awsconnect-0.0.1-SNAPSHOT.jar"]
