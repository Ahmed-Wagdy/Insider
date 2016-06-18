package utilities

import twitter4j.Status
import twitter4j.conf.ConfigurationBuilder


/**
  * Created by droidman on 24/05/16.
  */
object TwitterHelper {
  val settings = new InsiderSettings()
  import settings._
  //configuration builder for twitter

  val conf = new ConfigurationBuilder()
    .setDebugEnabled(true)
    .setOAuthConsumerKey(OAuthConsumerKey)
    .setOAuthConsumerSecret(OAuthConsumerSecret)
    .setOAuthAccessToken(OAuthAccessToken)
    .setOAuthAccessTokenSecret(OAuthAccessTokenSecret)


  case class Tweet(
                    id: Long,
                    createdAt: String,
                    text: String,
                    source: String,
                    retweetCount: Int,
                    favoutiteCount: Int,
                    isFavourited: Boolean,
                    isRetweeted: Boolean,
                    user: User
                  ) extends Serializable


  object Tweet {
    def apply(status: Status): Tweet = {
      Tweet(
        id = status.getId,
        createdAt = status.getCreatedAt.toString,
        text = status.getText,
        source = status.getSource,
        retweetCount = status.getRetweetCount,
        favoutiteCount = status.getFavoriteCount,
        isFavourited = status.isFavorited,
        isRetweeted = status.isRetweeted,
        user = User(status)
      )
    }
  }


  case class User(
                   id: Long,
                   name: String,
                   screeanName: String,
                   followersCount: Int,
                   location: String,
                   isVerified: Boolean
                 ) extends Serializable

  object User {
    def apply(status: Status) : User = {
      User(
        id = status.getUser.getId,
        name = status.getUser.getName,
        screeanName = status.getUser.getScreenName,
        followersCount = status.getUser.getFollowersCount,
        location = status.getUser.getLocation,
        isVerified = status.getUser.isVerified
      )
    }
  }

  case class StartCollecting(keyword :String) extends Serializable
  case class ProduceTweetToKafka(tweet: Tweet, topic :String)

}
