package org.techdelivery.property.spray

import spray.json._
import spray.routing.{Route, HttpService}
import spray.http.StatusCode._
import spray.http.{HttpEntity, HttpResponse}
import spray.http.MediaTypes._
import reactivemongo.bson.{BSONNull, BSONString, BSONObjectID, BSONDocument}
import scala.concurrent.Future
import org.techdelivery.property.entity.{MongoRegistryRecordFactory, RegistryRecord, MongoRegistryRecord}
import scala.util.{Failure, Success}
import reactivemongo.api.collections.default.BSONCollection

import scala.concurrent.ExecutionContext.Implicits.global

import org.techdelivery.property.entity.RegistryRecordProtocol._
import org.techdelivery.property.entity.RecordMapper._
import org.techdelivery.property.spray.CoordinatesHelper._
import akka.event.LoggingAdapter
import spray.http.Uri.Query
import reactivemongo.api.QueryOpts

trait PropertyServiceRoute extends HttpService {

  def route = propertyResource ~ propertyQuery

  protected def mongoCollection: BSONCollection

  protected def logger: LoggingAdapter

  protected def runFilterFuture(filter: BSONDocument, pagination: Pagination = Pagination()): Future[List[MongoRegistryRecord]] = {
    val opts = new QueryOpts(skipN = pagination.page * pagination.limit)
    mongoCollection.find(filter).options(opts).cursor[MongoRegistryRecord].toList(pagination.limit)
  }

  def propertyResource : Route = pathPrefix("property" / Segment) { id =>
    get {
      try {
        val filter = BSONDocument("_id" -> BSONObjectID(id))
        ctx => runFilterFuture(filter) onComplete {
          case Success(list) => {
            list match {
              case record :: Nil => {
                ctx.complete(HttpEntity(`application/json`, record.toJson.toString))
              }
              case record :: xs => {
                logger.error("Get " + mongoCollection.name + "[" + id + "] returned " + list.size + " matches instead of 1")
                ctx.complete(HttpResponse(status = 500))
              }
              case Nil => {
                logger.debug("Property {0} not found", id)
                ctx.complete {
                  HttpResponse(status = 404)
                }
              }
            }
          }
          case Failure(f) => ctx.complete {
            HttpResponse(status = 503, entity = f.getMessage)
          }
        }
      } catch {
        case iae: IllegalArgumentException => complete {
          HttpResponse(status = 404)
        }
        case e: Exception => {
          logger.warning(e.getMessage);
          logger.debug(e.getMessage, e);
          complete {
            HttpResponse(status = 501)
          }
        }
      }

    } ~ put { ctx =>
      try {
            val record = MongoRegistryRecordFactory(id, extractRecord(ctx.request.entity))
            val op = mongoCollection save (record)
            op onComplete {
              case Success(o) => ctx.complete( HttpResponse(status = 200, entity = record.toJson.toString) )
              case Failure(t) => logger.info(t.getMessage, t); ctx.complete( HttpResponse(status = 503) )
            }
        } catch {
        case e: Exception => {
          logger.warning(e.getMessage); logger.debug(e.getMessage, e); ctx.complete(HttpResponse(status = 503))
        }
      }
    } ~ delete {
      val op = mongoCollection remove (BSONDocument("_id" -> BSONObjectID(id)))
      ctx => op onComplete {
        case Success(o) => ctx.complete(HttpResponse(status = 200))
        case Failure(t) => {
          logger.info(t.getMessage, t);
          ctx.complete(HttpResponse(status = 503))

        }
        case _ => ctx.complete(HttpResponse(status = 404))
      }
    }
  }

  def propertyQuery : Route = path("property") {
    get {
      ctx => {
        val query = ctx.request.uri.query
        val filter = query_to_bson(query)
        val pagination = Pagination(query)
        runFilterFuture(filter, pagination) onComplete {
          case Success(list) => ctx.complete( HttpResponse(status = 200, entity = HttpEntity(`application/json`, list.toJson.toString ) ) )
          case Failure(f) => ctx.complete( HttpResponse(status = 503, entity = f.getMessage) )
        }
      }
    } ~
    post { ctx =>
      try {
        val entity = ctx.request.entity
        val record: RegistryRecord = extractRecord(entity)
        val op = mongoCollection insert (extractRecord(entity))
        op onComplete {
          case Success(o) => ctx.complete( HttpResponse(status = 201, entity = record.toJson.toString) )
          case Failure(t) => logger.info("Error", t); ctx.complete( HttpResponse(status = 503) )
        }
      } catch {
        case e: Exception => {
          logger.warning(e.getMessage); logger.debug(e.getMessage, e); ctx.complete( HttpResponse(status = 503) )
        }
      }
    }
  }

  case class Pagination(page: Int, limit: Int)

  object Pagination {
    def default_page = "0"

    def default_limit = "100"

    val max_limit: Int = 500
    val default_pagination = new Pagination(page = default_page.toInt, limit = default_limit.toInt)

    def apply(query_params: Query): Pagination = {
      val page = query_params getOrElse("page", default_page) toInt
      val parsed_limit = query_params getOrElse("limit", default_limit) toInt
      val limit = parsed_limit match {
        case x if x < 1 => default_limit.toInt
        case x if x > max_limit => max_limit
        case x => x
      }
      new Pagination(page = page, limit = limit)
    }

    def apply(): Pagination = default_pagination
  }


  private def query_to_bson(query: Query): BSONDocument = {
    val query_opts_fields = Set("page", "limit")

    def q(remaining_query: List[(String, List[String])], doc: BSONDocument): BSONDocument = {
      remaining_query match {
        case Nil => doc
        case (k, v) :: xs => {
          if (query_opts_fields contains k)
            q(xs, doc)
          else {
            val d: BSONDocument = (k, v) match {
              case ("box", List(b)) => bounding_box_query(b)
              case (_, List()) => BSONDocument(k -> BSONNull)
              case (_, a :: as ) => BSONDocument(k -> BSONString(a))
            }
            q(xs, doc ++ d)
          }
        }
      }
    }
    q(query.toMultiMap.toList, BSONDocument())
  }

  def extractRecord(entity: HttpEntity): RegistryRecord = {
    val body = entity.asString
    val jsonData = body.asJson
    val record: RegistryRecord = jsonData.convertTo[RegistryRecord]
    record
  }
}
