package utilities

import akka.actor.{ActorIdentity, ActorRef, Actor}
import com.google.gson.GsonBuilder
import twitter4j._
import utilities.SparkHelper.InitializeStream
import utilities.TwitterHelper.{ProduceTweetToKafka, StartCollecting, Tweet, User}

/**
 * Created by droidman on 22/05/16.
 */
class CollectTweetsActor(twitterStreamFactory: TwitterStreamFactory, supervisorActor: ActorRef) extends Actor{


  val twitterStream = twitterStreamFactory.getInstance()
  twitterStream.addListener(simpleStatusListener)

  var topic :String = null

  println(self.path)


  override def receive = {
    case StartCollecting(keyword) =>
      println("keyword received")
      startStream(keyword)
      topic = keyword

  }


  def startStream(keyWord: String) = {
    println("stream started")
    twitterStream.cleanUp()

    twitterStream.filter(new FilterQuery().track(keyWord))

    supervisorActor ! InitializeStream
  }


  def simpleStatusListener = new StatusListener() {

    def onStatus(status: Status) {
      if (status.getLang == "en") {
        //create Tweet object from each status
        val tweet = Tweet(status)
        //send the tweet back to the supervisor actor
        supervisorActor ! ProduceTweetToKafka(tweet, topic)
      }
    }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }



}
