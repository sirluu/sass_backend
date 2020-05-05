#!/bin/bash
cd common-service/
gradle clean build
cd ..
cd stockholm-service-test2/
gradle profileSetup -Penvironment=test2 clean build -x test
cd ..
cd stockholm-service-test1/
gradle profileSetup -Penvironment=test1 clean build -x test
cd ..
docker-compose build
docker-compose up
