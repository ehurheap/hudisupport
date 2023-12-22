import sbt._

object Dependencies {

  object version {
    val jvm = "1.8"
    val scala = "2.12.10"
    val munit = "0.7.29"
    // spark
    val scalatest = "3.2.12"
    val spark = "3.3.0"
    val hudi = "0.13.0"
    val json4s = "3.7.0-M11"
    val avro4s = "4.0.13"

    // jackson
    val jackson = "2.13.3"

    // testing
    val scalacheck = "1.16.0"

    // lock provider
    val dynamoDBClient = "1.1.0"
    val awsSDK = "1.12.264"

    val guava = "31.0-jre"
  }

  // testing
  val munit = "org.scalameta" %% "munit" % version.munit
  val scalacheck = "org.scalacheck" %% "scalacheck" % version.scalacheck
  val specs2 = "org.specs2" %% "specs2-core" % "4.16.1"

  // spark
  val sparkAvro = "org.apache.spark" %% "spark-avro" % version.spark
  val sparkCore = "org.apache.spark" %% "spark-core" % version.spark
  val sparkSql = "org.apache.spark" %% "spark-sql" % version.spark
  val sparkSqlKafka = "org.apache.spark" %% "spark-sql-kafka-0-10" % version.spark
  val sparkTokenProviderKafka =
    "org.apache.spark" %% "spark-token-provider-kafka-0-10" % version.spark

  // hudi
  val hudiSpark = "org.apache.hudi" %% "hudi-spark3.3-bundle" % version.hudi
  val hudiAws = "org.apache.hudi" % "hudi-aws" % version.hudi

  val avro4s = "com.sksamuel.avro4s" %% "avro4s-core" % version.avro4s
  val json4sCore = "org.json4s" %% "json4s-core" % version.json4s
  val json4sNative = "org.json4s" %% "json4s-native" % version.json4s
  val json4sAst = "org.json4s" %% "json4s-ast" % version.json4s

  // jackson json
  val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % version.jackson
  val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % version.jackson
  val jacksonAnnotations = "com.fasterxml.jackson.module" %% "jackson-annotations" % version.jackson
  val jacksonScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % version.jackson

  // lock provider
  val dynamoDBClient = "com.amazonaws" % "dynamodb-lock-client" % version.dynamoDBClient
  val dynamoDBSDK = "com.amazonaws" % "aws-java-sdk-dynamodb" % version.awsSDK
  val awsSDKCore = "com.amazonaws" % "aws-java-sdk-core" % version.awsSDK

  // hadoop stuff because ide won't run tests without it
  val hadoopCommon = "org.apache.hadoop" % "hadoop-common" % "3.3.2"
  val hadoopAuth = "org.apache.hadoop" % "hadoop-auth" % "3.3.2"

}
