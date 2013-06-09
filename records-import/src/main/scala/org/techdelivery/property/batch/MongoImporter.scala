package org.techdelivery.property.batch

import akka.actor.Actor
import reactivemongo.api.Collection
import org.techdelivery.property.batch.domain.RecordMapper._
import reactivemongo.api.DefaultDB
import org.techdelivery.property.batch.domain.RegistryRecord
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.Success
import scala.util.Failure

class MongoImporter(db: DefaultDB) extends Actor {

  val collection = db.collection("property")
  def receive = {
    case record: RegistryRecord => {
      val result = collection.insert(record)
      result onComplete {
        case Success(success) => {
          
        }
        case Failure(error) => {
          
        }
      }
    }
  }
}