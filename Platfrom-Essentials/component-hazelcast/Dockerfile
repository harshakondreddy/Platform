FROM adoptopenjdk:11.0.6_10-jre-hotspot

MAINTAINER Honeywell

# add application jar (the Spring Boot fat JAR) as main.j
ADD build/libs/component-hazelcast-2.0.0.jar /main.jar

# Add entrypoint.sh at the root of the container
ADD entrypoint.sh /entrypoint.sh

# Make sure entrypoint.sh is executable
RUN chmod +x /entrypoint.sh

# All Spring Boot components expose 8080 as the container port
EXPOSE 8080

# All Spring Boot components expose 8000 as the container java debug port
EXPOSE 8000

# Execute entrypoint.sh as the entrypoint command
ENTRYPOINT ["sh","/entrypoint.sh"]

# Start the Spring Boot fat JAR with the given JAVA_OPTS
# JAVA_OPTS takes "$MEM_OPTS $LOG_OPTS $GC_OPTS", default value is set in Entrypoint.sh file and can override at runtime
CMD exec java $JAVA_OPTS -jar /main.jar

