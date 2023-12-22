package com.sample.delete

import org.apache.hudi.client.SparkRDDWriteClient
import org.apache.hudi.client.common.HoodieSparkEngineContext
import org.apache.hudi.common.config.HoodieMetadataConfig
import org.apache.hudi.common.engine.HoodieEngineContext
import org.apache.hudi.common.model.{HoodieFailedWritesCleaningPolicy, HoodieKey, WriteConcurrencyMode}
import org.apache.hudi.config._
import org.apache.spark.api.java.{JavaRDD, JavaSparkContext}
import org.apache.spark.sql.SparkSession

import java.util.Properties
import scala.collection.JavaConverters._

object UUIDRecordKeyDeleter {
  // Deletes all records with the provided record keys without triggering compaction.
  def deleteRecords(tablePath: String,
                    recordKeys: JavaRDD[HoodieKey]
                   )(implicit
                     spark: SparkSession
                   ) = {

    val writerConfig = getConfig(tablePath)
    println(s"***writerConfig***")
    writerConfig.getProps().asScala.foreach(println)

    val engineContext: HoodieEngineContext = new HoodieSparkEngineContext(
      JavaSparkContext.fromSparkContext(spark.sparkContext)
    )

    val writeClient = new SparkRDDWriteClient(engineContext, writerConfig)
    var deleteInstant: String = ""

    try {
      deleteInstant = writeClient.startCommit()
      val success = writeClient.delete(recordKeys, deleteInstant)
      success.collect().asScala
    } catch {
      case t: Throwable =>
        println(s"Delete operation failed for instant $deleteInstant due to ${t.getCause}")
        println(s"Caused by ${t.getCause.getCause}")
        t.getCause.getCause.getStackTrace.map(_.toString).foreach(println)
        sys.exit(1)
    } finally {
      println(s"Finished delete operation for instant $deleteInstant")
      writeClient.close()
    }
  }

  def getConfig(tablePath: String): HoodieWriteConfig = {
    val lockProperties = new Properties()
    val lockOptionsMap =  Map[String, String](
      HoodieLockConfig.LOCK_PROVIDER_CLASS_NAME
        .key() -> "org.apache.hudi.aws.transaction.lock.DynamoDBBasedLockProvider",
      DynamoDbBasedLockConfig.DYNAMODB_ENDPOINT_URL.key() -> "http://127.0.0.1:8011",
      DynamoDbBasedLockConfig.DYNAMODB_LOCK_TABLE_NAME.key() -> "datalake-locks",
      DynamoDbBasedLockConfig.DYNAMODB_LOCK_PARTITION_KEY
        .key() -> s"users-changes-local",
      DynamoDbBasedLockConfig.DYNAMODB_LOCK_REGION.key() -> "us-east-1",
      HoodieWriteConfig.WRITE_CONCURRENCY_MODE
        .key() -> WriteConcurrencyMode.OPTIMISTIC_CONCURRENCY_CONTROL.name(),
      HoodieCleanConfig.FAILED_WRITES_CLEANER_POLICY
        .key() -> HoodieFailedWritesCleaningPolicy.LAZY.name())
    lockProperties.putAll(lockOptionsMap.asJava)
    HoodieWriteConfig
      .newBuilder()
      .withCompactionConfig(
        HoodieCompactionConfig
          .newBuilder()
          .withInlineCompaction(false)
          .withScheduleInlineCompaction(false)
          .withMaxNumDeltaCommitsBeforeCompaction(0)
          .build()
      )
      .withLockConfig(HoodieLockConfig.newBuilder().fromProperties(lockProperties).build())
      .withArchivalConfig(HoodieArchivalConfig.newBuilder().withAutoArchive(false).build())
      .withCleanConfig(HoodieCleanConfig.newBuilder().withAutoClean(false).build())
      .withMetadataConfig(HoodieMetadataConfig.newBuilder().enable(false).build())
      .withDeleteParallelism(100)
      .withPath(tablePath)
      .forTable("datalakeRecord.tableName")
      .build()
  }

}
