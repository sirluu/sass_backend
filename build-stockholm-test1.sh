#!/bin/bash
cd stockholm-service-test1/
gradle profileSetup -Penvironment=test1 clean build -x test
docker-compose build
docker-compose up