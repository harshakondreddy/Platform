docker run --name hazlecase-mgmt -d -p 18000:8080  -e  JAVA_OPTS="-Dhazelcast.mc.allowMultipleLogin=true" igs-wms-docker-stable-local.artifactory-na.honeywell.com/hazelcast-mc:gold-3.12.7
