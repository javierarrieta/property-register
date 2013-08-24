package org.techdelivery.property.entity

import java.lang.Math.{sqrt,pow}
import spray.json.DefaultJsonProtocol
import reactivemongo.bson.{BSONDouble, BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Coordinates(x: Double,y:Double) {
  def distance(coord: Coordinates): Double = sqrt(pow(coord.x - x, 2) + pow(coord.y - y, 2))
  def <->(coord: Coordinates): Double = distance(coord)
}

case class Box(c1: Coordinates, c2: Coordinates) {
  def area = (c1.x - c2.x) * (c1.x - c2.x) + (c1.y - c2.y)*(c1.y - c2.y)
  def diagonal = c1.distance(c2)
  def width = Math.abs(c1.x - c2.x)
  def height = Math.abs(c1.y - c2.y)
}

object Box {
  def apply( c1x: Double, c1y: Double, c2x: Double, c2y: Double ): Box = {
    new Box(Coordinates(c1x,c1y), Coordinates(c2x,c2y))
  }
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