#!/bin/bash
DOCKER_DIR=./src/main/docker
docker-compose -f $DOCKER_DIR/keycloak.yml -f $DOCKER_DIR/jhipster-registry.yml -f $DOCKER_DIR/postgresql.yml up -d
