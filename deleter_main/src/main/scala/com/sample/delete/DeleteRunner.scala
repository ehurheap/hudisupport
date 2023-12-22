package com.sample.delete

import org.apache.hudi.DataSourceWriteOptions
import org.apache.hudi.DataSourceWriteOptions.{
  BULK_INSERT_OPERATION_OPT_VAL,
  HIVE_STYLE_PARTITIONING,
  MOR_TABLE_TYPE_OPT_VAL
}
import org.apache.hudi.common.config.HoodieMetadataConfig
import org.apache.hudi.common.model.{HoodieKey, HoodieRecord}
import org.apache.hudi.config.{HoodieCleanConfig, HoodieCompactionConfig, HoodieWriteConfig}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, Encoder, Encoders, Row, SparkSession}
import org.apache.spark.sql.hudi.command.UuidKeyGenerator
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

import java.io.File
import scala.concurrent.duration.{Deadline, DurationInt}

object DeleteRunner {
  val tableName = "users"
  val baseHudiPath: String = s"target/hudidata/${this.getClass.getName}"
  val absHudiDataPath: String = new File(baseHudiPath).getAbsolutePath
  val checkpointPath: String = s"$absHudiDataPath/checkpoints/${tableName}"
  val tablePath: String = s"$absHudiDataPath/tables/${tableName}"
  val publicDirPath: String = new File(s"$absHudiDataPath/public").getAbsolutePath

  implicit val spark = getSpark("deleterunnerApp")

  def main(args: Array[String]): Unit = {

    // create simple dataframe of (col1: Integer, col2: String)
    val schema = StructType(
      Array(
        StructField("col1", IntegerType, true),
        StructField("col2", StringType, true)
      )
    )
    val records = for (i <- 1 to 5) yield Row(i, i.toString)
    val df = spark.createDataFrame(spark.sparkContext.parallelize(records), schema)
    println(s"****----- writing to table path: $tablePath")
    // save to hudi
    df.write
      .format("hudi")
      .options(writeOptions)
      .mode("append")
      .save(tablePath)
    val savedRecords = readTable()
    savedRecords.show()

    // assemble RDD[HoodieKey] with keys to be deleted
    implicit val hoodieKeyEncoder: Encoder[HoodieKey] =
      Encoders.bean(classOf[org.apache.hudi.common.model.HoodieKey])
    val keysToDelete = savedRecords
      .filter(col("col1").isin(1, 2, 3))
      .select(HoodieRecord.RECORD_KEY_METADATA_FIELD, HoodieRecord.PARTITION_PATH_METADATA_FIELD)
      .map { row =>
        {
          val recordKey = row.getString(0)
          val partitionPath = row.getString(1)
          new org.apache.hudi.common.model.HoodieKey(recordKey, partitionPath)
        }
      }
      .toJavaRDD

    // attempt delete
    UUIDRecordKeyDeleter.deleteRecords(tablePath, keysToDelete)

    val remaining = readTable().count()
    println(s"Found ${remaining} remaining")
  }

  def writeOptions: Map[String, String] =
    Map[String, String](
      HoodieWriteConfig.TBL_NAME.key -> "users",
      DataSourceWriteOptions.KEYGENERATOR_CLASS_NAME.key -> classOf[UuidKeyGenerator].getName,
      DataSourceWriteOptions.RECORDKEY_FIELD.key -> "col1",
      DataSourceWriteOptions.PARTITIONPATH_FIELD.key -> "col1",
      DataSourceWriteOptions.OPERATION.key -> BULK_INSERT_OPERATION_OPT_VAL,
      DataSourceWriteOptions.TABLE_TYPE.key -> MOR_TABLE_TYPE_OPT_VAL,
      HoodieMetadataConfig.ENABLE.key -> "false",
      HIVE_STYLE_PARTITIONING.key -> "true",
      "hoodie.bulkinsert.shuffle.parallelism" -> "2",
      "hoodie.metadata.enable" -> "false",
      "hoodie.bulkinsert.sort.mode" -> "NONE",
      "hoodie.combine.before.insert" -> "false",
      "hoodie.datasource.write.row.writer.enable" -> "false",
      HoodieCleanConfig.AUTO_CLEAN.key() -> "false",
      HoodieCleanConfig.ASYNC_CLEAN.key() -> "false",
      HoodieCompactionConfig.INLINE_COMPACT.key() -> "false",
      HoodieCompactionConfig.SCHEDULE_INLINE_COMPACT.key() -> "false",
      DataSourceWriteOptions.ASYNC_COMPACT_ENABLE.key() -> "false"
    )

  def readTable()(implicit spark: SparkSession): DataFrame =
    spark
      .read
      .format("org.apache.hudi.Spark32PlusDefaultSource")
      .option("hoodie.datasource.query.type", "snapshot")
      .load(s"file://$tablePath")

  def getSpark(appName: String): SparkSession = {
    SparkSession
      .builder()
      .appName(appName)
      .master("local[1]")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .getOrCreate()
  }

  def readUntilFoundOrTimeout(timeoutSeconds: Int, foundMinimum: Int = 1)(
    implicit
    spark: SparkSession
  ): Boolean = {
    val maxWait: Deadline = timeoutSeconds.seconds.fromNow
    var found: Boolean = false
    while (maxWait.hasTimeLeft() && !found) {
      val readResults: DataFrame = readTable()
      if (readResults.count >= foundMinimum) found = true
      else Thread.sleep(1000)
    }
    found
  }
}
