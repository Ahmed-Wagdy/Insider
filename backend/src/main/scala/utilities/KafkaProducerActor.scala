package utilities

import java.util

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.google.gson.GsonBuilder
import utilities.TwitterHelper.{ProduceTweetToKafka, Tweet}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

/**
  * Created by droidman on 22/05/16.
  */
class KafkaProducerActor(settings: InsiderSettings) extends Actor {

  println("producer created")

  //properties for the kafka producer
  val props = new util.HashMap[String, Object]()

  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, settings.bootstrap_servers)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, settings.value_serializer)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, settings.key_serializer)

  val producer = new KafkaProducer[String, String](props)

  val builder = new GsonBuilder
  val gson = builder.create()

  def produceTweet(tweet: Tweet, topic :String) :Unit = {

    val message = new ProducerRecord[String, String](topic, gson.toJson(tweet))

    producer.send(message)
  }

  override def receive :Actor.Receive = {
    case ProduceTweetToKafka(t, k) => produceTweet(t, k)
  }
}
