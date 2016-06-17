package utilities

import akka.actor.{ActorRef, Actor}
import com.google.gson.Gson
import kafka.serializer.{StringDecoder, DefaultDecoder}
import org.apache.spark.{SparkContext, SparkConf}

import org.apache.spark.streaming.{Milliseconds, Minutes, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils

import com.datastax.spark.connector.streaming._


import PolarityBasic._


/**
  * Created by droidman on 22/05/16.
  */
class KafkaStreamingActor(ssc: StreamingContext, settings: InsiderSettings, topic: String, supervisor: ActorRef) extends Actor with Serializable{

  import settings._

  override def receive = {
    case _ =>
  }


  val kafkaStream = {

    //    val topics = new TopicAndPartition("twitter1", 0)

    //create a stream for each kafka partition to parallelize the read process from kafka
    val streams = (1 to noOfPartitions) map { _ =>
      //create a stream from last offset
      KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set(topic)).map(_._2)
    }
    //unify the streams into single Dstream
    val unifiedStream = ssc.union(streams)

    //set higher repartition value for production. spark runs a task for each partition on a single core
    //controlling no. of partitions will allow us to control the processing performance and parallelism
    //unifiedStream.repartition(2)

    unifiedStream
  }

    val gson = new Gson()

  //  val androidCount = kafkaStream.map(json => gson.fromJson(json, classOf[Tweet]).source).filter(_.contains("Twitter for Android")).count()
  //  kafkaStream.map(json => gson.fromJson(json, classOf[Tweet]).text)
  //      .flatMap(_.split(" ")).map(word => (word, 1L)).reduceByKeyAndWindow(_ + _, _ - _, Minutes(10), Seconds(2), 2).print()

  //  kafkaStream.map(gson.fromJson(_, classOf[Tweet]).source).filter(_.contains("iPhone")).print()

  //  kafkaStream.saveAsTextFiles("test1")
kafkaStream.map{tweet=>

  new Tuple2 (tweet,evaluate(tweet))}.saveToCassandra("insider",topic)

  ssc.start()
  ssc.checkpoint(streamingCheckpoint) // a check point must be specified for spark streaming
  ssc.awaitTermination()

}
