package org.techdelivery.property.batch.parser

import java.text.DateFormat
import java.text.SimpleDateFormat
import scala.util.matching.Regex
import java.lang.Long
import scala.runtime.RichBoolean
import java.util.Date
import org.techdelivery.property.entity.RegistryRecord

object RecordParser {
	val formatter = new SimpleDateFormat("dd/MM/yyyy")
	  
	implicit def parseLine(line: String): RegistryRecord = {
	  val fields = line.split(",").toArray
	  return RegistryRecord(cleanDate(fields(0)), fields(1), fields(2), fields(3),
	      cleanBD( fields(4) ), cleanBoolean(fields(5)), cleanBoolean(fields(6)), fields(7))
	}
	
	private def cleanBD( value: String): BigDecimal = BigDecimal(value.replaceAll("[^\\d.]+", ""))
	
	private def cleanBoolean( value: String): Boolean = value.toLowerCase match {
    case "true" => true
    case "yes"  => true
    case _ => false
  }
	
	private def cleanDate( value: String): Date = formatter.parse(value.replaceAll("\"", ""))
}