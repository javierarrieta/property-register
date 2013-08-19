package org.techdelivery.property.settings

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._
import org.slf4j.LoggerFactory

object configuration {
  private val log = LoggerFactory.getLogger(this.getClass)

  private val defaultConf = ConfigFactory.load
  val conf = ConfigFactory.load("mongo-app").withFallback(defaultConf)

  log.debug(conf.toString)

  lazy val servers = conf.getStringList("mongo.servers").asScala.toSeq
  lazy val db = conf.getString("mongo.database")
  lazy val propertyCollectionName = conf.getString("mongo.collection.property")
  lazy val checksumCollectionName = conf.getString("mongo.collection.checksum")
  lazy val geocoderToken = conf.getString("geocoder.token")
}
