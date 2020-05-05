#!/bin/bash
cd stockholm-service-test2/
gradle profileSetup -Penvironment=test2 clean build -x test
docker-compose build
docker-compose up