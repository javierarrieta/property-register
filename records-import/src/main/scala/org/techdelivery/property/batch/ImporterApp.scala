package org.techdelivery.property.batch

import scala.io.Source
import scala.io.Codec
import org.techdelivery.property.batch.parser.csvParser
import akka.actor.{ActorSystem, Props}
import org.techdelivery.property.mongo.MongoImporter
import org.techdelivery.property.mongo.propertyMongoDB._
import com.typesafe.scalalogging.slf4j.Logging

object ImporterApp extends App with Logging {

  //Initialize actors
  val system = ActorSystem("importer")
  val importer = system.actorOf(Props(new MongoImporter(propertyCollection)))
  val recordParser = system.actorOf(Props(new ChecksumCheckerActor(importer)))
  
  args.foreach { importRegistries(_) }

//  recordParser ! PoisonPill
//  importer ! PoisonPill
//  propertyMongoDB shutdown
  
  def importRegistries(file: String) = {
    val lines = Source.fromFile(file)(Codec.ISO8859).getLines().drop(1)
    val records = lines.map(csvParser.parse(_)(0))
    records.foreach { recordParser ! _ }
  }
  
}