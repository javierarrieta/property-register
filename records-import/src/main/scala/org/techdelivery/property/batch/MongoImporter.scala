package org.techdelivery.property.batch

import akka.actor.{ActorLogging, Actor}
import org.techdelivery.property.batch.domain.RecordMapper._
import reactivemongo.api.DefaultDB
import org.techdelivery.property.batch.domain.RegistryRecord
import org.techdelivery.property.batch.domain.RegistryRecord._
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.Success
import scala.util.Failure

class MongoImporter(db: DefaultDB) extends Actor with ActorLogging {

  val collection = db.collection("property")
  def receive = {
    case record: RegistryRecord => {
      val properRecord = cleanRecord(record)
      val result = collection.insert(properRecord)
      result onComplete {
        case Success(success) => {
          log info (record.toString)
        }
        case Failure(error) => {
          log error (error, record.toString)
        }
      }
    }
    case message: AnyRef => log error("Message not expected " + message.getClass.getName)
  }
}