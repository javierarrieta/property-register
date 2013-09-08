package org.techdelivery.property.spray

import org.scalatest.FunSuite
import org.techdelivery.property.entity.{Box, Coordinates}
import reactivemongo.bson.{BSONDocument, BSONArray}

import CoordinatesHelper._

class CoordinatesHelperTest extends FunSuite {

  val (c1x,c1y,c2x,c2y) = (23423.3244687, -29.3222, -0.23, 99.999)
  val box = Box( c1x, c1y, c2x, c2y )

  test("Double parse") {
    def assert_value(s:String): Unit = assert( DoubleParser(s) == s.toDouble )

    assert_value("32.352552")
    assert_value("-4232.43234")
    assert_value("0")
    assert_value("21")
    assert_value("-22")
    //assert_value(".3")
    intercept[NumberFormatException] { assert_value("-.03") }
    intercept[NumberFormatException] { assert_value("-") }
    intercept[NumberFormatException] { assert_value("ab") }
    intercept[NumberFormatException] { assert_value("") }
    intercept[NumberFormatException] { assert_value("0.1.") }
    intercept[NumberFormatException] { assert_value("0.2.1") }
  }

  test("Coordinates parse") {
    def assert_coords(x:String, y:String): Unit = {
      assert( CoordinatesParser("[" + x + "," + y + "]") == Coordinates(x.toDouble, y.toDouble) )
      assert( CoordinatesParser("  [  " + x + " ,  " + y + "  ]  ") == Coordinates(x.toDouble, y.toDouble) )
    }

    assert_coords("3","4")
    assert_coords("-342.423423","2342.433333")
    assert_coords("-342","-2342")
    intercept[NumberFormatException] { assert_coords("","-4.333")}
    intercept[NumberFormatException] { assert_coords("32","-")}
    intercept[NumberFormatException] { assert_coords("","")}
  }

  test("Box parser") {
    def assert_box( c1x: String, c1y: String, c2x: String, c2y: String ): Unit = {
      assert(
        BoxParser("[[" + c1x + "," + c1y + "],[" + c2x + "," + c2y + "]]")
          ==
        Box(Coordinates(c1x.toDouble, c1y.toDouble), Coordinates(c2x.toDouble, c2y.toDouble))
      )
    }

    assert_box("3.234","333.233422","-0.32322","-232.0")
    assert_box("-3.234","-333","-0.32322","-232.0")
    intercept[NumberFormatException] { BoxParser("ewwww")}
    intercept[NumberFormatException] { assert_box("3","22","1a", "3") }
  }

  test("Box to BSON") {
    assert( to_bson( box ) == BSONDocument( "$box" -> BSONArray( BSONArray(c1x,c1y), BSONArray(c2x,c2y) ) ) )
  }

  test("Box Query from Box") {
    assert(
      bounding_box_query(box) == BSONDocument(
        "loc" -> BSONDocument(
          "$geoWithin" -> to_bson(box)
        )
      )
    )
  }

  test("Box Query from String") {
    assert(
      bounding_box_query("[[" + c1x + "," + c1y + "],[" + c2x + "," + c2y + "]]") == BSONDocument(
        "loc" -> BSONDocument(
          "$geoWithin" -> to_bson(box)
        )
      )
    )
  }
}
