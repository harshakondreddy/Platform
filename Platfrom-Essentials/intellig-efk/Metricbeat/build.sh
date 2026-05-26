#!/bin/bash
set -e

DOCKER_REPO_NAME=docker-local.repo.intellig.local
IMAGE=intellig-metricbeat

docker build -t $DOCKER_REPO_NAME/$IMAGE:6.8.3