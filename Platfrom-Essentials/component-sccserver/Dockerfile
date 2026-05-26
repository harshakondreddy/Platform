FROM adoptopenjdk:11.0.6_10-jre-hotspot
VOLUME /tmp
ADD build/libs/spring-cloud-config-server-2.0.0.jar /main.jar

# All Spring Boot components expose 8080 as the container port
EXPOSE 8080
# All Spring Boot components expose 8000 as the container java debug port
EXPOSE 8000
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/main.jar"]
