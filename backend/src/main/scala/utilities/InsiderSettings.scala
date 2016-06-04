package utilities

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by droidman on 27/05/16.
  */

/**
  * (Type safe) application settings. First attempts to acquire from the runtime environment.
  * If a setting is not found, attempts to acquire from -D java system properties,
  * finally falling back to a default config.
  *
  * Settings in the environment such as: SPARK_HA_MASTER="spark://host1@port1,host2@port2..." are picked up first.
  *
  * Settings from the command line in -D will override settings in the deploy environment.
  * For example: sbt -Dspark.master="spark://host3@port3,host4@port4..."
  *
  * If you have not yet used Typesafe Config before, you can pass in overrides like so:
  *
  * {{{
  *     new Settings(ConfigFactory.parseString("""spark.master = "some.ip""""))
  * }}}
  *
  * Any of these can also be overriden by your own application.conf.
  *
  * @param conf Optional config for test
  */

final class InsiderSettings(conf: Option[Config] = None) extends Serializable {


  val rootConfig = conf match {
    case Some(c) => c.withFallback(ConfigFactory.load())
    case _ => ConfigFactory.load
  }

  val twitter = rootConfig.getConfig("Twitter")

  val OAuthConsumerKey = twitter.getString("OAuthConsumerKey")
  val OAuthConsumerSecret = twitter.getString("OAuthConsumerSecret")
  val OAuthAccessToken = twitter.getString("OAuthAccessToken")
  val OAuthAccessTokenSecret = twitter.getString("OAuthAccessTokenSecret")

  val insider = rootConfig.getConfig("Insider")

  val appName = insider.getString("app-name")

  val kafka = rootConfig.getConfig("kafka")

  val noOfPartitions = kafka.getInt("number.of.partitions")

  val bootstrap_servers = kafka.getString("bootstrab_servers")
  val key_serializer = kafka.getString("serializer")
  val value_serializer = kafka.getString("serializer")

  val spark = rootConfig.getConfig("spark")

  val sparkMaster = spark.getString("master")
  val StreamingBatchInterval = spark.getInt("streaming.batch.interval")
  val streamingCheckpoint = spark.getString("spark.checkpoint.dir")

  val kafkaParams = Map(
    "zookeeper.connect" -> kafka.getString("zookeeper.connect"),
    "group.id" -> kafka.getString("group.id"),
    "zookeeper.connection.timeout.ms" -> kafka.getInt("zookeeper.connection.timeout.ms").toString,
    "metadata.broker.list" -> kafka.getString("metadata.broker.list"))

}
