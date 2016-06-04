package utilities

import spray.routing.SimpleRoutingApp
import akka.actor.{PoisonPill, ActorRef, Actor, Props}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import utilities.SparkHelper.{KillActor, InitializeStream}
import utilities.TwitterHelper.{StartCollecting, ProduceTweetToKafka, Tweet}
//import twitter4j.TwitterStreamFactory
import akka.pattern.gracefulStop
import scala.concurrent.duration._

/**
  * Created by droidman on 27/05/16.
  */
class SupervisorActor(sc :SparkContext, settings: InsiderSettings) extends Actor with SimpleRoutingApp{

  import settings._



  val ssc = new StreamingContext(sc, Milliseconds(StreamingBatchInterval))



  override def receive = {
    case s :String =>
      println(s)

    case InitializeStream(s) =>
      context.actorOf(Props(new KafkaStreamingActor(ssc, settings, s, self)), s)

    case KillActor(s) =>
      val toKillActor = context.actorSelection("/user/supervisor-actor/"+s)
      toKillActor ! PoisonPill

    case PoisonPill => gracefulShutdown()
  }


  def gracefulShutdown(): Unit = {
    context.children foreach (c => gracefulStop(c, 2.seconds))
  }


}
