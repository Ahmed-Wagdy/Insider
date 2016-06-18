name := "utilities"

version := "1.0"

scalaVersion := "2.10.4"

resolvers += "spray repo" at "http://repo.spray.io"







libraryDependencies ++= Seq(

  "org.apache.spark" % "spark-sql_2.10" % "1.6.1",
  "com.datastax.spark" %% "spark-cassandra-connector" % "1.6.0-M2"
)

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.4"

libraryDependencies += "org.json4s" % "json4s-native_2.10" % "3.3.0"

libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.9.0.1"

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.6.1"

libraryDependencies += "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.1"

libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "1.6.1"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.15"

libraryDependencies += "com.google.code.gson" % "gson" % "2.6.2"

compileOrder := CompileOrder.JavaThenScala

libraryDependencies ++= {

  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-routing" % sprayV withSources() withJavadoc(),
    "io.spray"            %%  "spray-client"     % sprayV withSources() withJavadoc(),
    "org.json4s"          %%  "json4s-native"  % "3.2.10"
  )
}

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }
    