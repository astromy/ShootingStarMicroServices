FROM eclipse-temurin:20-jre-alpine as builder
WORKDIR extracted
ADD target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM openjdk:22-windowsservercore-ltsc2022
WORKDIR application
COPY --from=builder extracted/dependencies/ ./
COPY --from=builder extracted/snapshot-dependencies/ ./
COPY --from=builder extracted/spring-boot-loader/ ./
COPY --from=builder extracted/application/ ./
EXPOSE 7013
CMD -p 7013
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

##FROM eclipse-temurin:17.0.4.1_1-jre as builder
#FROM openjdk:17-jdk-alpine as builder
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} shootingStar-setup.jar
#RUN java -Djarmode=layertools -jar shootingStar-setup.jar extract
#FROM openjdk:17-jdk-alpine