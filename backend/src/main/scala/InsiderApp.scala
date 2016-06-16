import akka.actor._
import akka.util.Timeout
import org.apache.spark.{SparkContext, SparkConf}
import utilities.TwitterHelper.StartCollecting
import utilities.{DBAccess, SupervisorActor, InsiderSettings}
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import akka.pattern.ask


/**
  * Created by droidman on 27/05/16.
  */
object InsiderApp extends App{

  val settings = new InsiderSettings()
  import settings._

  val conf = new SparkConf()
    .setAppName(appName)
    .setMaster(sparkMaster) //changed in production. the number specifies the no. of cores for spark to run on

  val sc = new SparkContext(conf)

  val system = ActorSystem(appName)


  val superVisor = system.actorOf(Props(new SupervisorActor(sc, settings)), "supervisor-actor")


}
