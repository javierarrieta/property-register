package org.techdelivery.property.batch

import scala.io.Source
import scala.io.Codec
import org.techdelivery.property.batch.parser.csvParser
import akka.actor.Props
import org.techdelivery.property.entity.{RegistryRecord, RegistryRecordLineParser}
import org.techdelivery.property.mongo.MongoImporter
import org.techdelivery.property.mongo.propertyMongoDB._
import com.typesafe.scalalogging.slf4j.Logging

object ImporterApp extends App with Logging {

  def registerLine(rec: RegistryRecord): Unit = { importer !  rec }
  
  val system = actorSystem
  
  val importer = system.actorOf(Props(new MongoImporter(propertyCollection)))
  
  args.foreach { importRegistries(_) }

  system.shutdown()
  
  def importRegistries(file: String) = {
    val lines = Source.fromFile(file)(Codec.ISO8859).getLines().drop(1)
    val records = lines.map(csvParser.parse(_)(0))
    records.foreach { line =>
      try {
        val record = RegistryRecordLineParser(line)
        val csum = Checksum(line)
        ChecksumChecker.runAfterCheck(csum, record, registerLine )
      } catch {
        case e: IllegalArgumentException => logger.warn(line + ": " + e.getMessage)
      }
    }
  }
  
}