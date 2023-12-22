import sbtassembly.MergeStrategy

val deps = Dependencies

name := "deleter"
version := "0.1"
scalaVersion := deps.version.scala

lazy val deleter_main = project
  .settings(
    name := "deleter-main",
    excludeDependencies ++= Seq(
      ExclusionRule("org.apache.logging.log4j", "log4j-to-slf4j"),
      ExclusionRule("org.apache.hudi", "hudi-common"),
    ),
    libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always,
    libraryDependencies ++= Seq(
      deps.sparkAvro,
      deps.sparkCore % Provided,
      deps.sparkSql,
      deps.hudiSpark % Provided,
      deps.hudiAws % Provided,
      deps.sparkTokenProviderKafka,
      deps.avro4s,
      deps.json4sCore,
      deps.json4sAst,
      deps.jacksonCore,
      deps.jacksonScala,
      deps.jacksonDatabind,
      deps.awsSDKCore,
      deps.dynamoDBSDK,
      deps.dynamoDBClient,
      deps.hadoopCommon % Provided,
      deps.hadoopAuth % Provided
    ),
    assemblyOptions
  )

lazy val assemblyOptions = Seq(
  assembly / assemblyMergeStrategy := {
    case PathList("git.properties", xs @ _*) => MergeStrategy.last
    case PathList("io","netty", xs @ _*) => MergeStrategy.last
    case PathList("javax", "inject", xs @ _*) => MergeStrategy.last
    case PathList("javax","annotation", xs @ _*) => MergeStrategy.last
    case PathList("jetty-dir.css") => MergeStrategy.last
    case PathList("META-INF", "native-image", "io.netty", xs @ _*) => MergeStrategy.last
    case PathList("META-INF","io.netty.versions.properties") => MergeStrategy.last
    case PathList("META-INF","versions","9","module-info.class") => MergeStrategy.last
    case PathList("META-INF","org","apache","logging","log4j","core","config","plugins","Log4j2Plugins.dat") => MergeStrategy.last
    case PathList("mime.types") => MergeStrategy.last
    case PathList("module-info.class") => MergeStrategy.last
    case PathList("mozilla", "public-suffix-list.txt", xs @ _*) => MergeStrategy.last
    case PathList("org","apache","commons","logging", xs @ _*) => MergeStrategy.last
    case PathList("org", "apache", "curator", xs @ _*) => MergeStrategy.last
    case PathList("org", "apache", "http", xs @ _*) => MergeStrategy.last
    case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class") => MergeStrategy.first
    case PathList("org", "aopalliance", xs @ _*) => MergeStrategy.last
    case PathList("org", "rocksdb", xs @ _*) => MergeStrategy.last
    case PathList("org","slf4j","impl","StaticLoggerBinder.class") => MergeStrategy.last
    case PathList("org","slf4j","impl","StaticMDCBinder.class") => MergeStrategy.last
    case PathList("org","slf4j","impl","StaticMarkerBinder.class") => MergeStrategy.last
    case x => (assembly / assemblyMergeStrategy).value(x)
  }
)