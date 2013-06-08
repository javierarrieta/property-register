package org.techdelivery.property.batch

import akka.actor.Actor
import reactivemongo.api.Collection
import org.techdelivery.property.batch.domain.RegistryRecord

class MongoImporter(collection: Collection) extends Actor {

  def receive = {
    case record: RegistryRecord => {
      val result = collection.insert(record)
      result onComplete {
        case Success(success) => {
          
        }
        case Fail(error) => {
          
        }
      }
    }
  }
}