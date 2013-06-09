package org.techdelivery.property.batch

import com.typesafe.config.ConfigFactory
import reactivemongo.api.MongoDriver
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits._
import scala.io.Source
import org.techdelivery.property.batch.domain.RecordParser
import scala.io.Codec
import org.techdelivery.property.batch.parser.csvParser

object ImporterApp extends App {

  val defaultConf = ConfigFactory.load
  lazy val conf = ConfigFactory.load("mongo-app").withFallback(defaultConf)
  
  implicit val mongo = new MongoDriver
  val connection = mongo.connection(conf.getStringList("mongo.servers").asScala.toSeq)
  val db = connection.db(conf.getString("mongo.database"))
  
  override def main(args: Array[String]) = {
    val lines = Source.fromFile(args(0))(Codec.ISO8859).getLines().drop(1)
    val records = lines.map(csvParser.parse(_)(0))
    records.foreach {
      println(_)
    }
  }
}