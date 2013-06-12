package org.techdelivery.property.entity

import java.util.Date
import reactivemongo.bson._
import reactivemongo.bson.BSONLong
import reactivemongo.bson.BSONDateTime
import reactivemongo.bson.BSONBoolean
import reactivemongo.bson.BSONString
import scala.Some
import spray.json._
import DefaultJsonProtocol._
import java.text.SimpleDateFormat
import org.joda.time.format.ISODateTimeFormat
import scala.math.BigDecimal
import scala.math.BigDecimal._

case class RegistryRecord(sale_date: Date, address: String, postal_code: String, county: String,
		price: BigDecimal, full_market_price: Boolean, vat_exclusive: Boolean, description: String)
					
case class MongoRegistryRecord(id: String, sale_date: Date, address: String, postal_code: String, county: String, 
		price: BigDecimal, full_market_price: Boolean, vat_exclusive: Boolean, description: String)

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
  implicit object RecordMapper extends BSONDocumentWriter[RegistryRecord] {
	  def write(record: RegistryRecord) : BSONDocument = BSONDocument(
	    "sale_date" -> BSONDateTime(record.sale_date.getTime()),
	    "address" -> BSONString(record.address),
	    "postal_code" -> BSONString(record.postal_code),
	    "county" -> BSONString(record.county),
	    "price" -> BSONLong((record.price * 100.0).toLong),
	    "full_market_price" -> BSONBoolean(record.full_market_price),
	    "vat_exclusive" -> BSONBoolean(record.vat_exclusive),
	    "description" -> BSONString(record.description)
	  )
  }
  implicit object MongoRecordMapper extends BSONDocumentWriter[MongoRegistryRecord] with BSONDocumentReader[MongoRegistryRecord] {

	  def write(record: MongoRegistryRecord) : BSONDocument = BSONDocument(
      "_id" -> BSONObjectID(record.id),
	    "sale_date" -> BSONDateTime(record.sale_date.getTime()),
	    "address" -> BSONString(record.address),
	    "postal_code" -> BSONString(record.postal_code),
	    "county" -> BSONString(record.county),
	    "price" -> BSONLong((record.price * 100.0).toLong),
	    "full_market_price" -> BSONBoolean(record.full_market_price),
	    "vat_exclusive" -> BSONBoolean(record.vat_exclusive),
	    "description" -> BSONString(record.description)
	  )

    def read(doc: BSONDocument) : MongoRegistryRecord = new MongoRegistryRecord(
	    doc.getAs[BSONObjectID]("_id").get.stringify,
      new Date(doc.getAs[BSONDateTime]("sale_date").map(_.value).get),
      doc.getAs[BSONString]("address").map(_.value).get,
      doc.getAs[BSONString]("postal_code").map(_.value).get,
      doc.getAs[BSONString]("county").map(_.value).get,
      BigDecimal(doc.getAs[BSONLong]("price").map(_.value).get) / 100,
      doc.getAs[BSONBoolean]("full_market_price").map(_.value).get,
      doc.getAs[BSONBoolean]("vat_exclusive").map(_.value).get,
      doc.getAs[BSONString]("description").map(_.value).get
    )
  }

  object RegistryRecordProtocol extends DefaultJsonProtocol {
    val format = ISODateTimeFormat.dateTime()
    implicit object DateJsonFormat extends JsonFormat[Date] {
      def write(d:Date) = JsString(format.print(d.getTime))
      def read(value: JsValue) = value match {
        case JsString(s) => format.parseDateTime(s).toDate
        case _ => throw new DeserializationException("Date Expected")
      }
    }
    implicit val recordFormat = jsonFormat9(MongoRegistryRecord)
  }

}