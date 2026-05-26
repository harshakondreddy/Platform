#!/bin/bash
set -e

DOCKER_REPO_NAME=docker-local.repo.intellig.local
IMAGE=intellig-fluentd

version=`cat VERSION`
echo "version: $version"

# run build
./build.sh

# tag image
docker tag $DOCKER_REPO_NAME/$IMAGE:gold $DOCKER_REPO_NAME/$IMAGE:$version

# push image
docker push $DOCKER_REPO_NAME/$IMAGE:gold
docker push $DOCKER_REPO_NAME/$IMAGE:$version