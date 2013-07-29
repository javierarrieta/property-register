package org.techdelivery.property.batch.coord

import akka.actor.{ActorRef, Actor, ActorLogging}
import java.net.URLEncoder
import spray.client.pipelining._
import scala.util.{Failure, Success}
import org.techdelivery.property.entity.{Coordinates, RegistryRecord}
import spray.http.HttpResponse
import CoordinatesExtractor._
import spray.json._

class CoordCloudMadeActor(importer: ActorRef, token:String) extends Actor with ActorLogging {

  def composeAddress(record: RegistryRecord) = List(record.address, record.postal_code, "Ireland") mkString ","

  def receive = {

    case record: RegistryRecord => {
      val system = context.system
      import system.dispatcher
      val origin = sender
      val address = composeAddress(record)
      val encodedAddress = URLEncoder.encode(address,"UTF-8")
      val uri = token + "/geocoding/v2/find.js?query=" + encodedAddress
      val pipeline = sendReceive
      pipeline(Get("http://geocoding.cloudmade.com/" + uri)).onComplete {
        case Success(a) => importer ! record.copy(coord = extractCoordinates(a))
        case Failure(error) => { log.error(error, error.getMessage) ; origin ! record }
      }
    }

  }
}

object CoordinatesExtractor {
  def extractCoordinates(response: HttpResponse): Option[Coordinates] = {
    val payload = response.entity.asString
    val js = payload.asJson.asJsObject
    val features = js.getFields("features")(0) match {
      case JsArray(elements) => elements.head
    }
    features.asJsObject.getFields("centroid")(0).asJsObject.getFields("coordinates")(0) match {
      case JsArray(List(x:JsNumber,y:JsNumber)) => Some(new Coordinates(x.value.doubleValue(), y.value.doubleValue()))
      case _ => None
    }
  }
}
