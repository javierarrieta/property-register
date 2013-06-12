package org.techdelivery.property.spray

import akka.actor.Actor
import spray.http.{HttpBody, HttpResponse, HttpRequest}
import spray.http.HttpHeaders._
import spray.http.HttpMethods._
import spray.http.MediaTypes._
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson.{BSONObjectID, BSONDocument}
import reactivemongo.api.collections.default.BSONCollection
import spray.json._
import scala.util.{Failure, Success}
import org.techdelivery.property.entity._
import RecordMapper._
import RegistryRecordProtocol._

class PropertyResource(collection: BSONCollection) extends Actor {
  val get_rx = "^\\/property\\/(\\w*)$".r

  def receive= {
    case HttpRequest(GET, path, _, _, _) => {
      val origin = sender
      path match {
        case get_rx(id) => {
          val filter = BSONDocument( "_id" -> BSONObjectID(id))
          val result = collection.find(filter).cursor[MongoRegistryRecord]
          val response = result.toList
          response onComplete {
            case Success(list) => {
              list match {
                case record :: Nil => origin ! HttpResponse( status = 200, entity = HttpBody(`application/json`,record.toJson.toString) )
                case Nil => origin ! HttpResponse( status = 404 )
              }
            } 
            case Failure(f) => origin ! HttpResponse( status = 503, entity = f.getMessage)
          }
        }
        case _ => origin ! HttpResponse(status = 404)
      }
    }
    case _ => sender ! HttpResponse(status = 404)

  }
}
