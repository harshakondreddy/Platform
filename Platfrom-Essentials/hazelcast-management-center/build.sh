#!/bin/bash
set -e

DOCKER_REPO_NAME=igs-wms-docker-stable-local.artifactory-na.honeywell.com
IMAGE=hazelcast-mc

docker build -t $DOCKER_REPO_NAME/$IMAGE:gold-3.12.7 .
