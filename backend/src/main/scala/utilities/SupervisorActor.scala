package utilities


import spray.routing.SimpleRoutingApp
import akka.actor._
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import utilities.SparkHelper.{InitializeStream, KillActor}
import utilities.TwitterHelper.{StartCollecting, ProduceTweetToKafka, Tweet}
//import twitter4j.TwitterStreamFactory
import akka.pattern.gracefulStop
import scala.concurrent.duration._

/**
  * Created by droidman on 27/05/16.
  */
class SupervisorActor(sc :SparkContext, settings: InsiderSettings) extends Actor with SimpleRoutingApp{

  case class searchParams(key:String, keytype:String)
  import settings._



  val ssc = new StreamingContext(sc, Milliseconds(StreamingBatchInterval))



  override def receive = {
    case s :String =>
      println(s)

    case InitializeStream(s) =>
      context.actorOf(Props(new KafkaStreamingActor(ssc, settings, s, self)), s)
      println(s, "streaming initialized")

    case KillActor(s) =>
      val toKillActor = context.actorSelection("/user/supervisor-actor/"+s)
      toKillActor ! PoisonPill

    case PoisonPill => gracefulShutdown()
  }


  def gracefulShutdown(): Unit = {
    context.children foreach (c => gracefulStop(c, 2.seconds))
  }

  implicit val actorSystem = ActorSystem()

  import actorSystem.dispatcher
  startServer(interface = "localhost", port = 8080) {
    post {
      path("search" / "twitter") {
        parameters("keyword".as[String]) { (keyword ) =>
           self ! InitializeStream(keyword)
          complete {
            "OK"

          }
        }
      }
    }

    }
  }
