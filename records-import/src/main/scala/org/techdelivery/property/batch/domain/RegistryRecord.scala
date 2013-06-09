package org.techdelivery.property.batch.domain

import java.util.Date
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDateTime
import reactivemongo.bson.BSONString
import reactivemongo.bson.BSONLong
import reactivemongo.bson.BSONBoolean

case class RegistryRecord(sale_date: Date, address: String, postal_code: String, county: String, price: Long,
					full_market_price: Boolean, vat_exclusive: Boolean, description: String)
					
case class MongoRegistryRecord(id: String, sale_date: Date, address: String, postal_code: String, county: String, price: Long,
					full_market_price: Boolean, vat_exclusive: Boolean, description: String)

object RecordMapper {
  implicit object MongoRecordMapper extends BSONDocumentWriter[RegistryRecord] {
  
	  def write(registry: RegistryRecord) : BSONDocument = BSONDocument(
	    "sale_date" -> BSONDateTime(registry.sale_date.getTime()),
	    "address" -> BSONString(registry.address),
	    "postal_code" -> BSONString(registry.postal_code),
	    "county" -> BSONString(registry.county),
	    "price" -> BSONLong(registry.price),
	    "full_market_price" -> BSONBoolean(registry.full_market_price),
	    "vat_exclusive" -> BSONBoolean(registry.vat_exclusive),
	    "description" -> BSONString(registry.description)
	  )
  }
}