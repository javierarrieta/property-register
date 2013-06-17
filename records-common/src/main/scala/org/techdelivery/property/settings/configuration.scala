package org.techdelivery.property.settings

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object configuration {
  private val defaultConf = ConfigFactory.load
  lazy val conf = ConfigFactory.load("mongo-app").withFallback(defaultConf)

  lazy val servers = conf.getStringList("mongo.servers").asScala.toSeq
  lazy val db = conf.getString("mongo.database")
  lazy val propertyCollectionName = conf.getString("mongo.collection.property")
  lazy val checksumCollectionName = conf.getString("mongo.collection.checksum")
}
