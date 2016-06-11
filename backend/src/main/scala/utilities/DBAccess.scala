package utilities

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.sql.cassandra.CassandraSQLContext
import org.apache.spark.{SparkContext, SparkConf}
import org.joda.time.DateTime

/**
  * Created by hesham on 11/06/16.
  */
object DBAccess {
  val conf = new SparkConf().set("spark.cassandra.connection.host", "127.0.0.1").setMaster("local")
  val sc = new SparkContext("local", "test", conf)

  val cc = new CassandraSQLContext(sc)

  def createkeyspace(ksname: String): Unit =
  {
    CassandraConnector(conf).withSessionDo{ session =>
      session.execute(s"CREATE KEYSPACE IF NOT EXISTS ${ksname} WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1 }")

    }
  }

  def createTwitterTable(ksname: String,tableName: String): Unit =
  {
    CassandraConnector(conf).withSessionDo { session =>
      // session.execute("DROP TABLE IF EXISTS test.sql_demo")
      session.execute( s"""CREATE TABLE IF NOT EXISTS $ksname.${tableName} (id INT PRIMARY KEY, text varchar, favoriteCount INT, retweetCount INT,createdAt timestamp)""")
    }

  }

  def insert(ksname: String,tableName: String,id: Int,text: String,favoriteCount: Int,retweetCount: Int,createdAt: DateTime): Unit =
  {
    CassandraConnector(conf).withSessionDo { session =>
      session.execute(s"""INSERT INTO $ksname.${tableName}(id, text, favoriteCount, retweetCount, createdAt ) VALUES (${id}, '${text}', ${favoriteCount}, ${retweetCount}, '${createdAt}')""")
    }

}
