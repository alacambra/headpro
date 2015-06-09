#!/bin/bash
RHA_HOME=/home/alacambra/rha
DB_PATH=${RHA_HOME}/db
BACKUP_PATH=${RHA_HOME}/backup
DOCKER_DB_PATH=/opt/jboss/wildfly/db
DOCKER_BACKUP_PATH=/opt/jboss/wildfly/db/backup

docker build --tag=rha ${RHA_HOME}/build
docker kill rha
docker rm rha

docker run --name rha -p 28080:8080 -p 29990:9990 -itd rha


# docker run -d -p 80:80 --name nginx-hub \
#  --link rha:rha \
#  alacambra/nginx:hub nginx

