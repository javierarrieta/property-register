package org.techdelivery.property.entity

import java.lang.Math.{sqrt,pow}
import spray.json.DefaultJsonProtocol
import reactivemongo.bson.{BSONDouble, BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Coordinates(x: Double,y:Double) {
  def distance(coord: Coordinates): Double = sqrt(pow(coord.x - x, 2) + pow(coord.y - y, 2))
  def <->(coord: Coordinates): Double = distance(coord)
}

trait CoordinatesProtocol extends DefaultJsonProtocol {
  implicit val jsonFormat = jsonFormat2(Coordinates)
}

object CoordinatesMapper {
  implicit object CoordinatesMapper extends BSONDocumentWriter[Coordinates] with BSONDocumentReader[Coordinates] {
    override def write(coord: Coordinates): BSONDocument = BSONDocument("lat" -> coord.x, "lng" -> coord.y)
    override def read(doc: BSONDocument): Coordinates = new Coordinates(
        doc.getAs[BSONDouble]("lat").map(_ value) get,
        doc.getAs[BSONDouble]("lng").map(_ value) get
      )
  }
}