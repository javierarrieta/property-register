package org.techdelivery.property.batch.domain

import java.text.DateFormat
import java.text.SimpleDateFormat
import scala.util.matching.Regex
import java.lang.Long
import scala.runtime.RichBoolean
import java.util.Date

object RecordParser {
	val formatter = new SimpleDateFormat("dd/MM/yyyy")
	  
	implicit def parseLine(line: String): RegistryRecord = {
	  val fields = line.split(",").toArray
	  return RegistryRecord(cleanDate(fields(0)), fields(1), fields(2), fields(3),
	      cleanLong( fields(4) ), cleanBoolean(fields(5)), cleanBoolean(fields(6)), fields(7))
	}
	
	private def cleanLong( value: String): Long = Long.parseLong(value.replaceAll("[^\\d.]+", ""),10)
	
	private def cleanBoolean( value: String): Boolean = value.toBoolean
	
	private def cleanDate( value: String): Date = formatter.parse(value.replaceAll("\"", ""))
}