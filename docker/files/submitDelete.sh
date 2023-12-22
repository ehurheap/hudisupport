#!/bin/bash
set -euxo pipefail

DELETE_JAR=/home/heap/deleter-main-assembly-0.1.0-SNAPSHOT.jar
MAIN_CLASS=com.sample.delete.DeleteRunner

heap_jars="$(find /home/heap/jars -path '*.jar' -printf '%p,' | sed 's/,$//g')"

/opt/bitnami/spark/bin/spark-submit \
 --master spark://localhost:7077 \
 --deploy-mode client \
 --conf spark.yarn.appMasterEnv.AWS_ACCESS_KEY_ID=DUMMYIDEXAMPL \
 --conf spark.yarn.appMasterEnv.AWS_SECRET_ACCESS_KEY=DUMMYEXAMPLEKEY \
 --conf spark.yarn.appMasterEnv.REGION=us-east-1 \
 --conf spark.yarn.submit.waitAppCompletion=true \
 --conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
 --conf spark.jars.ivy=/tmp/.ivy \
 --conf spark.cores.max=2 \
 --driver-memory 1G \
 --executor-memory 2G \
 --jars "${heap_jars}" \
 --class ${MAIN_CLASS} \
 ${DELETE_JAR}
