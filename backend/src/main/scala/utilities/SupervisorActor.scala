package utilities


import org.json4s.native.Serialization
import org.json4s.ShortTypeHints
import spray.routing.SimpleRoutingApp
import akka.actor._
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import utilities.SparkHelper.{senResult, InitializeStream, KillActor}
import utilities.TwitterHelper.{StartCollecting, ProduceTweetToKafka, Tweet}
//import twitter4j.TwitterStreamFactory
import akka.pattern.gracefulStop
import scala.concurrent.duration._

/**
  * Created by droidman on 27/05/16.
  */
class SupervisorActor(sc :SparkContext, settings: InsiderSettings) extends Actor with SimpleRoutingApp{


  private implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[senResult])))

  def toJson(result: senResult): String = Serialization.writePretty(result)

  val topicsMap = scala.collection.mutable.Map[String,senResult]()

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

  startServer(interface = "localhost", port = 8080) {

    get {
      path("getresult" / Segment ) { topic =>
        complete{
//          topicsMap(topic).posCount=1
          toJson(topicsMap(topic))


        }
      }
    } ~
    post {
      path("search" / "twitter") {
        parameters("keyword".as[String]) { (keyword ) =>
           self ! InitializeStream(keyword)
          topicsMap += keyword -> senResult(0,0)

          complete {
            "OK"

          }
        }
      }
    }

    }
  }
