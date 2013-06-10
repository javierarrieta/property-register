package org.techdelivery.property.batch

import com.typesafe.config.ConfigFactory
import reactivemongo.api.MongoDriver
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits._
import scala.io.Source
import org.techdelivery.property.batch.domain.{RegistryRecord, RecordParser}
import scala.io.Codec
import org.techdelivery.property.batch.parser.csvParser
import akka.actor.{PoisonPill, ActorSystem, Props}

object ImporterApp extends App {
  
  val system = ActorSystem("system")

  val defaultConf = ConfigFactory.load
  lazy val conf = ConfigFactory.load("mongo-app").withFallback(defaultConf)
  
  implicit val mongo = new MongoDriver
  val connection = mongo.connection(conf.getStringList("mongo.servers").asScala.toSeq)
  val db = connection.db(conf.getString("mongo.database"))
  
  val importer = system.actorOf(Props(new MongoImporter(db)))
  
  args.foreach { importRegistries(_) }

  importer ! PoisonPill
  
  def importRegistries(file: String) = {
    val lines = Source.fromFile(file)(Codec.ISO8859).getLines().drop(1)
    val records = lines.map(csvParser.parse(_)(0))
    records.foreach { importer !  RegistryRecord(_) }
  }
  
  
}