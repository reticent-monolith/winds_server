#!/bin/bash

git checkout master
git merge dev
gradle clean
gradle build
gradle installDist
docker build -t reticentmonolith/backend:latest .
docker push reticentmonolith/backend:latest
