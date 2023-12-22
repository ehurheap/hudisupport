FROM bitnami/spark:3.3.0
ARG RELPATH

USER root

RUN install_packages curl

WORKDIR /home/heap/jars
RUN curl -O https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/1.12.233/aws-java-sdk-bundle-1.12.233.jar
RUN curl -O https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/3.3.3/hadoop-aws-3.3.3.jar
RUN curl -O https://repo1.maven.org/maven2/org/apache/hudi/hudi-spark3.3-bundle_2.12/0.13.0/hudi-spark3.3-bundle_2.12-0.13.0.jar
RUN curl -O https://repo1.maven.org/maven2/org/apache/hudi/hudi-aws/0.13.0/hudi-aws-0.13.0.jar
RUN curl -O https://repo1.maven.org/maven2/org/apache/spark/spark-sql-kafka-0-10_2.12/3.3.0/spark-sql-kafka-0-10_2.12-3.3.0.jar
RUN curl -O https://repo1.maven.org/maven2/org/apache/spark/spark-token-provider-kafka-0-10_2.12/3.3.0/spark-token-provider-kafka-0-10_2.12-3.3.0.jar

WORKDIR /home/heap
COPY deleter_main/target/scala-2.12/deleter-main-assembly-0.1.0-SNAPSHOT.jar .
COPY docker/files/submitDelete.sh .

