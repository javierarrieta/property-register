package org.techdelivery.property.batch

import scala.io.Source
import scala.io.Codec
import org.techdelivery.property.batch.parser.csvParser
import akka.actor.{PoisonPill,Props}
import org.techdelivery.property.entity.RegistryRecord
import org.techdelivery.property.mongo.MongoImporter
import org.techdelivery.property.mongo.propertyMongoDB._

object ImporterApp extends App {
  
  val system = actorSystem
  
  val importer = system.actorOf(Props(new MongoImporter(propertyCollection)))
  
  args.foreach { importRegistries(_) }

  system.shutdown()
  
  def importRegistries(file: String) = {
    val lines = Source.fromFile(file)(Codec.ISO8859).getLines().drop(1)
    val records = lines.map(csvParser.parse(_)(0))
    records.foreach { importer !  RegistryRecord(_) }
  }
  
}