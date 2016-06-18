package utilities

import java.util
import java.util.Properties

import akka.actor.{ActorRef, Actor}
import com.google.gson.Gson
import kafka.consumer.{Whitelist, Consumer, ConsumerConfig}
import kafka.serializer.{StringDecoder, DefaultDecoder}
import org.apache.kafka.clients.consumer.{ConsumerRecords, ConsumerRecord, KafkaConsumer}
import org.apache.spark.{SparkContext, SparkConf}

import org.apache.spark.streaming.{Milliseconds, Minutes, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils

import com.datastax.spark.connector.streaming._


import PolarityBasic._


/**
  * Created by droidman on 22/05/16.
  */
class KafkaStreamingActor(settings: InsiderSettings, topic: String, supervisor: ActorRef, db: DBAccess) extends Actor{

  import settings._

  override def receive = {
    case _ =>
  }

  kafkaConsumerProps.put("group.id", topic)

  val config = new ConsumerConfig(kafkaConsumerProps)

  val consumer = Consumer.create(config)

  val topicCounts = Map(topic->1)

  val consumerMap = consumer.createMessageStreams(topicCounts)

  val consumerIterator = consumerMap.get(topic).get.head.iterator()

  val msgs = consumerIterator.map(_.message())

  msgs.foreach{
    msg=>
      db.insert("insider",topic, new String(msg),evaluate(new String(msg)))
  }




  //    val gson = new Gson()

  //  val androidCount = kafkaStream.map(json => gson.fromJson(json, classOf[Tweet]).source).filter(_.contains("Twitter for Android")).count()
  //  kafkaStream.map(json => gson.fromJson(json, classOf[Tweet]).text)
  //      .flatMap(_.split(" ")).map(word => (word, 1L)).reduceByKeyAndWindow(_ + _, _ - _, Minutes(10), Seconds(2), 2).print()

  //  kafkaStream.map(gson.fromJson(_, classOf[Tweet]).source).filter(_.contains("iPhone")).print()

  //  kafkaStream.saveAsTextFiles("test1")
  //
  //  new Tuple2 (tweet,evaluate(tweet))}.saveToCassandra("insider",topic)
  //


}
