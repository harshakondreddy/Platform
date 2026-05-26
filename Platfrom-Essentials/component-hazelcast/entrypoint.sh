#!/bin/bash

#Default Active-Profile is set to 'docker'
ACTIVE_PROFILES="docker"
export SPRING_PROFILES_ACTIVE="$ACTIVE_PROFILES"


# ################################################ #
# Setup identifying information for this component #
# ################################################ #

# Define APPLICATION_NAME if not already defined
if [ "$APPLICATION_NAME" == "" ]
then
    AN=reds-cache-server
    export APPLICATION_NAME=$AN
fi

# Define APPLICATION_INSTANCE_ID if not already defined
if [ "$APPLICATION_INSTANCE_ID" == "" ]
then
    export APPLICATION_INSTANCE_ID="$APPLICATION_NAME-$HOSTNAME"
fi

# Define EUREKA_INSTANCE_METADATAMAP_INSTANCEID if not already defined
if [ "$EUREKA_INSTANCE_METADATAMAP_INSTANCEID" == "" ]
then
    export EUREKA_INSTANCE_METADATAMAP_INSTANCEID="$APPLICATION_INSTANCE_ID"
fi

# ################################################################ #
# Setup externally mounted directories for this component instance #
# ################################################################ #

# Set WORKING_DIR and WORKING_APP_DIR directories
if [ "$WORKING_DIR" == "" ]
then
    export WORKING_DIR="/honeywell/wes/$APPLICATION_NAME"
fi

# create $WORKING_DIR (regardless of whether it exists or not)
mkdir -p $WORKING_DIR
echo "Created WORKING_DIR $WORKING_DIR"

if [ "$WORKING_APP_DIR" == "" ]
then
    export WORKING_APP_DIR="$WORKING_DIR/instances/$HOSTNAME"
fi

# create $WORKING_APP_DIR (regardless of whether it exists or not)
mkdir -p $WORKING_APP_DIR
echo "Created WORKING_APP_DIR $WORKING_APP_DIR"

#
# create a directory inside WORKING_APP_DIR to store
# GC logs and other such dynamic dumps that require persistence
# across container lifecycle
#
export JVM_OUTPUT_DIR="$WORKING_APP_DIR/jvmout"
mkdir -p $JVM_OUTPUT_DIR
mkdir -p $JVM_OUTPUT_DIR/logs
mkdir -p $JVM_OUTPUT_DIR/dumps
echo "Created JVM_OUTPUT_DIR $JVM_OUTPUT_DIR and child directories 'logs' and 'dumps' under it"

# ################################## #
# Set JAVA_OPTS environment variable #
# ################################## #
# set the default log level for the application code
if [ "$DEFAULT_LOG_LEVEL" == "" ]
then
    export DEFAULT_LOG_LEVEL=INFO
fi

#if MEM_OPTS is not defined
if [ "$MEM_OPTS" == "" ]
then
    # then set the default JAVA_OPTS settings
    export MEM_OPTS="-Xmx256m -Xss1024k"
fi

#if LOG_OPTS is not defined
if [ "$LOG_OPTS" == "" ]
then
    # then set the default JAVA_OPTS settings
    export LOG_OPTS="-Dlogging.level.com.hon.reds=$DEFAULT_LOG_LEVEL -Dlogging.level.com.netflix=$DEFAULT_LOG_LEVEL"
fi

#if JVM_OPTS is not defined, set the default JVM_OPTS
if [ "$JVM_OPTS" == "" ]
then
    export JVM_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xlog:gc*=info,safepoint*=info:file=$JVM_OUTPUT_DIR/logs/jvm.log:time,uptime,level,tags:filecount=10,filesize=50M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$JVM_OUTPUT_DIR/dumps/oom.hprof -XX:+DisableExplicitGC -XX:+UseStringDeduplication"
fi
export JAVA_OPTS="$MEM_OPTS $LOG_OPTS $JVM_OPTS"

# if DEBUG_JAVA is defined
if [ "$DEBUG_JAVA" == "true" ]
then
    if [ "$DEBUG_SUSPEND" == "true" ]
    then
        export JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000"
    else
        export JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000"
    fi
fi

echo "Exported JAVA_OPTS to $JAVA_OPTS"

echo "Set DB_CLEAN Flag to False"
export DB_CLEAN="false"

# ################################ #
# Output the environment variables #
# ################################ #
echo "---------------------"
echo "ENVIRONMENT VARIABLES"
echo "---------------------"
env

exec "$@"

