package org.techdelivery.property.batch.domain

import java.util.Date
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDateTime
import reactivemongo.bson.BSONString
import reactivemongo.bson.BSONLong
import reactivemongo.bson.BSONBoolean
import java.text.SimpleDateFormat

case class RegistryRecord(sale_date: Date, address: String, postal_code: String, county: String, price: Long,
					full_market_price: Boolean, vat_exclusive: Boolean, description: String)
					
case class MongoRegistryRecord(id: String, sale_date: Date, address: String, postal_code: String, county: String, price: Long,
					full_market_price: Boolean, vat_exclusive: Boolean, description: String)

object RegistryRecord {
	val formatter = new SimpleDateFormat("dd/MM/yyyy")

	private def cleanLong( value: String): Long = BigDecimal(value.replaceAll("[^\\d.]+", "")).longValue

  private def cleanBoolean( value: String): Boolean = value.toLowerCase match {
    case "true" => true
    case "yes"  => true
    case _ => false
  }

	private def cleanDate( value: String): Date = formatter.parse(value.replaceAll("\"", ""))

  def apply(fields:List[String]): RegistryRecord = new RegistryRecord(cleanDate(fields(0)), fields(1), fields(2), fields(3),
    cleanLong( fields(4) ), cleanBoolean(fields(5)), cleanBoolean(fields(6)), fields(7))

  def cleanRecord(original: RegistryRecord) : RegistryRecord = {
    val rx = "\\,\\ *(\\w*\\ \\d{1,2})$".r
    rx.findFirstMatchIn(original.address) match {
      case Some(pc) => original.postal_code match {
        case "" => original.copy(postal_code = pc.group(1))
        case _ => original
      }
      case None => original
    }
  }
}
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