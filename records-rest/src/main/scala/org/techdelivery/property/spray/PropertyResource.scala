package org.techdelivery.property.spray

import akka.actor.{ActorLogging, Actor}
import spray.http.{HttpEntity, HttpBody, HttpResponse, HttpRequest}
import spray.http.HttpHeaders._
import spray.http.HttpMethods._
import spray.http.MediaTypes._
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson.{BSONObjectID, BSONDocument}
import reactivemongo.api.collections.default.BSONCollection
import spray.json._
import scala.util.{Failure, Success}
import org.techdelivery.property.entity.{MongoRegistryRecordFactory, MongoRegistryRecord, RegistryRecord}
import org.techdelivery.property.entity.RecordMapper._
import org.techdelivery.property.entity.RegistryRecordProtocol._

class PropertyResource(collection: BSONCollection) extends Actor with ActorLogging {
  val get_rx = "^\\/property\\/(\\w*)$".r

  def receive = {
    case HttpRequest(GET, path, _, _, _) => {
      val origin = sender
      path match {
        case get_rx(id) => {
          try {
            val filter = BSONDocument("_id" -> BSONObjectID(id))
            val result = collection.find(filter).cursor[MongoRegistryRecord]
            val response = result.toList
            response onComplete {
              case Success(list) => {
                list match {
                  case record :: Nil => origin ! HttpResponse(status = 200, entity = HttpBody(`application/json`, record.toJson.toString))
                  case record :: xs => log.error("Get " + collection.name + "[" + id + "] returned " + list.size + " matches instead of 1")
                  case Nil => origin ! HttpResponse(status = 404)
                }
              }
              case Failure(f) => origin ! HttpResponse(status = 503, entity = f.getMessage)
            }
          } catch {
            case iae: IllegalArgumentException => origin ! HttpResponse(status = 404)
            case e: Exception => {
              log.warning(e.getMessage); log.debug(e.getMessage, e); origin ! HttpResponse(status = 500)
            }
          }
        }
        case _ => origin ! HttpResponse(status = 404)
      }
    }
    case HttpRequest(POST, "/property", headers, entity, _) => {
      val origin = sender
      try {
        val record: RegistryRecord = extractRecord(entity)
        val op = collection insert (extractRecord(entity))
        op onComplete {
          case Success(o) => origin ! HttpResponse(status = 201, entity = record.toJson.toString)
          case Failure(t) => log.info("Error", t); origin ! HttpResponse(status = 503)
        }
      } catch {
        case e: Exception => {
          log.warning(e.getMessage); log.debug(e.getMessage, e); origin ! HttpResponse(status = 503)
        }
      }
    }
    case HttpRequest(PUT, path, headers, entity, _) => {
      val origin = sender
      try {
        path match {
          case get_rx(id) => {
            val record = MongoRegistryRecordFactory(id, extractRecord(entity))
            val op = collection save (record)
            op onComplete {
              case Success(o) => origin ! HttpResponse(status = 200, entity = record.toJson.toString)
              case Failure(t) => log.info(t.getMessage, t); origin ! HttpResponse(status = 503)
            }
          }
          case _ => origin ! HttpResponse(status = 404)
        }
      } catch {
        case e: Exception => {
          log.warning(e.getMessage); log.debug(e.getMessage, e); origin ! HttpResponse(status = 503)
        }
      }
    }
    case HttpRequest(DELETE, path, headers,_, _) => {
      val origin = sender
      try {
        path match {
          case get_rx(id) => {
            val op = collection remove(BSONDocument("_id" -> BSONObjectID(id)))
            op onComplete {
              case Success(o) => origin ! HttpResponse(status = 200)
              case Failure(t) => log.info(t.getMessage, t); origin ! HttpResponse(status = 503)
            }
          }
          case _ => origin ! HttpResponse(status = 404)
        }
      } catch {
        case e: Exception => {
          log.warning(e.getMessage); log.debug(e.getMessage, e); origin ! HttpResponse(status = 503)
        }
      }
    }
    case _ => sender ! HttpResponse(status = 404)
  }

  def extractRecord(entity: HttpEntity): RegistryRecord = {
    val body = entity.asString
    val jsonData = body.asJson
    val record: RegistryRecord = jsonData.convertTo[RegistryRecord]
    record
  }
}
