To Reproduce
--
1. clone this repo

`git clone git@github.com:ehurheap/hudisupport.git`

2. run
```
cd hudisupport
./run.sh
```

What run.sh does
--
- assembles the deleter jar 
- builds docker image for spark
- runs docker compose with dynamodb_local and spark
- execs into spark master container and submits `DeleteRunner` main class to spark-submit (see `submitDelete.sh` script)
- `DeleteRunner` saves some records to local hudi tablepath
- `DeleteRunner` then attempts to delete some of those records keys using SparkRDDWriteClient
- Delete fails with `Delete operation failed for instant YYYYMMDDHHSSSSSSS due to org.apache.spark.SparkException: Task not serializable
  Caused by java.io.NotSerializableException: org.apache.hudi.aws.transaction.lock.DynamoDBBasedLockProvider`