package org.techdelivery.propery.batch.coord

import org.scalatest.FunSuite
import spray.http.{HttpEntity, HttpResponse}
import org.techdelivery.property.batch.coord.CoordinatesExtractor._
import org.techdelivery.property.entity.Coordinates

class CoordImporterTest extends FunSuite {

  test ("ded") {
    val payload = """{"found": 2, "bounds": [[26.69404, 6.08621], [53.34095, 127.82463]], "features": [{"id": 18450517,"centroid": {"type":"POINT","coordinates":[26.71017, 127.80666]},"bounds": [[26.69404, 127.78869], [26.72631, 127.82463]],"properties": {"name:en": "Ie", "name": "伊江村  Ie ", "is_in:island": "Okinawa", "name:ja_rm": "Ie son", "wikipedia": "http://en.wikipedia.org/wiki/Ie,_Okinawa", "place": "village", "osm_id": "653758009", "osm_element": "node", "is_in:region": "沖縄県  Okinawa Prefecture ", "population": "5128"},"type": "Feature"},{"id": 2162222,"centroid": {"type":"POINT","coordinates":[53.3302, 6.10418]},"bounds": [[53.31944, 6.08621], [53.34095, 6.12215]],"properties": {"name": "Ee", "osm_element": "node", "postal_code": "9131", "place": "village", "osm_id": "48394571", "name:fy": "Ie", "is_in": "NL", "population": "1000"},"type": "Feature"}], "type": "FeatureCollection", "crs": {"type": "EPSG", "properties": {"code": 4326, "coordinate_order": [0, 1]}}}"""
    val response = HttpResponse(entity = HttpEntity(payload))
    val coord = extractCoordinates(response)

    assert(coord == Some(Coordinates(26.71017, 127.80666)))
  }
}