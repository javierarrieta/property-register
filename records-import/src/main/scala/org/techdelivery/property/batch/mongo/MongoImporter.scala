package org.techdelivery.property.mongo

import akka.actor.{ActorLogging, Actor}
import org.techdelivery.property.entity.RegistryRecord
import org.techdelivery.property.entity.RegistryRecordLineParser._
import org.techdelivery.property.entity.RecordMapper._
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.Success
import scala.util.Failure
import reactivemongo.api.collections.default.BSONCollection

class MongoImporter(propCollection: BSONCollection) extends Actor with ActorLogging {
  def receive = {
    case record: RegistryRecord => {
      val properRecord = cleanRecord(record)
      insertRecord(properRecord)
    }
    case message: AnyRef => log error("Message not expected " + message.getClass.getName)
  }
  
  private def insertRecord(record: RegistryRecord): Unit = {
    val result = propCollection.insert(record)
    result onComplete {
      case Success(success) => {
        log info (record.toString)
      }
      case Failure(error) => {
        log error (error, record.toString)
      }
    }
  }
  
  
}