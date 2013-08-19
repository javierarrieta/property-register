package org.techdelivery.property.spray

import reactivemongo.bson.{BSONDouble, BSONArray, BSONDocument}
import org.techdelivery.property.entity.{Coordinates, Box}
import scala.util.parsing.combinator.RegexParsers
import scala._
import reactivemongo.bson.BSONDouble

trait CoordsParser extends RegexParsers {
  def number: Parser[Double] = """(-)?\d+(\.\d*)?""".r ^^ { _.toDouble }
  def coords: Parser[Coordinates] = "[" ~ number ~ "," ~ number ~ "]" ^^ {
    case p1 ~ x ~ c ~ y ~ p2 => Coordinates(x,y)
  }
  def box: Parser[Box] = "[" ~ coords ~ "," ~ coords ~ "]" ^^ {
    case p1 ~ c1 ~ c ~ c2 ~ p2 => Box(c1,c2)
  }
}

object DoubleParser extends CoordsParser {
  def apply(s: String): Double = parseAll(number, s) match {
    case Success(result, _) => result
    case failure: NoSuccess => throw new NumberFormatException(failure.msg)
  }
}

object CoordinatesParser extends CoordsParser {
  def apply(s: String): Coordinates = parseAll(coords, s) match {
    case Success(result, _) => result
    case failure: NoSuccess => throw new NumberFormatException(failure.msg)
  }
}

object BoxParser extends CoordsParser {
  def apply(s: String): Box = parseAll(box, s) match {
    case Success(result, _) => result
    case failure: NoSuccess => throw new NumberFormatException(failure.msg)
  }
}

trait CoordinatesHelper {

  def bounding_box_query(s: String): BSONDocument = bounding_box_query(BoxParser(s))

  def bounding_box_query(box: Box): BSONDocument = BSONDocument(
    "loc" -> BSONDocument(
      "$geoWithin" -> to_bson(box)
    )
  )

  def to_bson(box: Box): BSONDocument = BSONDocument(
    "$box" -> BSONArray(
      BSONArray(BSONDouble(box.c1.x), BSONDouble(box.c1.y)),
      BSONArray(BSONDouble(box.c2.x), BSONDouble(box.c2.y))
    )
  )
}
