#!/bin/bash
RHA_HOME=/home/alacambra/hp

docker build -t alacambra/hp ${RHA_HOME}/build
docker kill hp
docker rm hp

docker run --name hp -p 20022:22 -p 28080:8080 -p 29990:9990 -itd alacambra/hp