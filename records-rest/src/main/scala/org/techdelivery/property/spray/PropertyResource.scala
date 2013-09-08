package org.techdelivery.property.spray

import akka.actor.{ActorLogging, Actor}
import spray.http._
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson._
import spray.json._
import org.techdelivery.property.entity.{MongoRegistryRecord, RegistryRecord}
import org.techdelivery.property.entity.RecordMapper._
import org.techdelivery.property.entity.RegistryRecordProtocol._
import scala.concurrent.Future
import reactivemongo.bson.BSONString
import scala.Some
import reactivemongo.api.QueryOpts
import reactivemongo.api.collections.default.BSONCollection

class PropertyResource
(collection: BSONCollection) extends Actor with PropertyServiceRoute with ActorLogging {

  def actorRefFactory = context

  def receive = runRoute(route)

  def logger = log

  protected def mongoCollection = collection

  /*
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
                  case record :: Nil => origin ! HttpResponse(status = 200, entity = HttpEntity(`application/json`, record.toJson.toString))
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
          val pagination = Pagination(query_params)
          runFilterFuture(filter, pagination) onComplete {
            case Success(list) => origin ! HttpResponse(status = 200, entity = HttpEntity(`application/json`, list.toJson.toString ))
            case Failure(f) => origin ! HttpResponse(status = 503, entity = f.getMessage)
          }
        }
        case _ => origin ! HttpResponse(status = 404)
      }
    }
    case HttpRequest(POST, Uri.Path("/property"), headers, entity, _) => {
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
*/
  protected def runFilterFuture(filter: BSONDocument, pagination: Pagination = Pagination()): Future[List[MongoRegistryRecord]] = {
    val opts = new QueryOpts(skipN = pagination.page * pagination.limit)
    collection.find(filter).options(opts).cursor[MongoRegistryRecord].toList(pagination.limit)
  }
}
