#!/bin/bash
set -e

DOCKER_REPO_NAME=docker-local.repo.intellig.local
IMAGE=intellig-fluentd

docker build -t $DOCKER_REPO_NAME/$IMAGE:gold .
