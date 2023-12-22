set -e

sbt deleter_main/assembly
docker build --file ./docker/spark.Dockerfile --tag sample_spark/latest .
docker-compose -f ./stack.yml up -d
export AWS_ACCESS_KEY_ID=DUMMYIDEXAMPL
export AWS_SECRET_ACCESS_KEY=DUMMYEXAMPLEKEY
export REGION=us-east-1
#env_vars="AWS_ACCESS_KEY_ID=DUMMYIDEXAMPLE AWS_SECRET_ACCESS_KEY=DUMMYEXAMPLEKEY REGION=us-east-1"
#class_path="/Users/lhurley/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/hudi/hudi-spark3.3-bundle_2.12/0.13.0/hudi-spark3.3-bundle_2.12-0.13.0.jar,/Users/lhurley/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/spark/spark-sql_2.12/3.3.0/spark-sql_2.12-3.3.0.jar"
#jar_path="./deleter_main/target/scala-2.12/deleter-main-assembly-0.1.0-SNAPSHOT.jar"
#cmd="java -cp $class_path -jar $jar_path com.sample.delete.DeleteRunner"
#echo $cmd
#$cmd

cmd="docker-compose -f ./stack.yml exec -it spark bash -c /home/heap/submitDelete.sh"
echo $cmd
$cmd
/home/heap/jars/delete-main-assembly-0.1.0-SNAPSHOT.jar not found