#!/bin/bash
set -e

sbt deleter_main/assembly
docker build --file ./docker/spark.Dockerfile --tag sample_spark/latest .
docker-compose -f ./stack.yml up -d
export AWS_ACCESS_KEY_ID=DUMMYIDEXAMPL
export AWS_SECRET_ACCESS_KEY=DUMMYEXAMPLEKEY
export REGION=us-east-1

cmd="docker-compose -f ./stack.yml exec -it spark bash -c /home/heap/submitDelete.sh"
echo $cmd
$cmd
