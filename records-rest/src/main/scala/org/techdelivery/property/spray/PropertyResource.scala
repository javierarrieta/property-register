package org.techdelivery.property.spray

import akka.actor.{ActorLogging, Actor}
import spray.http.{HttpEntity, HttpBody, HttpResponse, HttpRequest}
import spray.http.HttpHeaders._
import spray.http.HttpMethods._
import spray.http.MediaTypes._
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson._
import reactivemongo.api.collections.default.BSONCollection
import spray.json._
import scala.util.{Failure, Success}
import org.techdelivery.property.entity.{MongoRegistryRecordFactory, MongoRegistryRecord, RegistryRecord}
import org.techdelivery.property.entity.RecordMapper._
import org.techdelivery.property.entity.RegistryRecordProtocol._
import org.techdelivery.property.entity.MongoRegistryRecord
import scala.util.Failure
import reactivemongo.bson.BSONString
import scala.Some
import org.techdelivery.property.entity.RegistryRecord
import spray.http.HttpResponse
import scala.util.Success
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.Future

class PropertyResource(collection: BSONCollection) extends Actor with ActorLogging {
  val get_rx = "^\\/property\\/(\\w*)$".r

  def receive = {
    case HttpRequest(GET, path, _, _, _) => {
      val origin = sender
      Resource(path) match {
        case Resource(List("property", id: String), _) => {
          try {
            val filter = BSONDocument("_id" -> BSONObjectID(id))
            val response = runFilterFuture(filter)
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
        case Resource(List("property"), query_params) => {
          val filter = query_to_bson(query_params)
          runFilterFuture(filter) onComplete {
            case Success(list) => origin ! HttpResponse(status = 200, entity = HttpBody(`application/json`, list.toJson.toString ))
            case Failure(f) => origin ! HttpResponse(status = 503, entity = f.getMessage)
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


  protected def runFilterFuture(filter: BSONDocument): Future[List[MongoRegistryRecord]] = {
    collection.find(filter).cursor[MongoRegistryRecord].toList
  }

  private def query_to_bson(query: Map[String,Option[String]]): BSONDocument = {
    def q(remaining_query: List[(String, Option[String])], doc: BSONDocument): BSONDocument = {
      remaining_query.toList match {
        case Nil => doc
        case (k,v) :: xs => {
          val value: BSONValue = v match { case None => BSONNull; case Some(a) => BSONString(a) }
          doc ++ q(xs, doc ++ BSONDocument(k -> value ))
        }
      }
    }
    q(query.toList, BSONDocument())
  }

  def extractRecord(entity: HttpEntity): RegistryRecord = {
    val body = entity.asString
    val jsonData = body.asJson
    val record: RegistryRecord = jsonData.convertTo[RegistryRecord]
    record
  }
}
