package org.techdelivery.property.mongo

import scala.concurrent.ExecutionContext.Implicits._
import reactivemongo.api.MongoDriver
import org.techdelivery.property.settings.configuration._
import reactivemongo.api.collections.default.BSONCollection

object propertyMongoDB {
  private implicit val mongo = new MongoDriver
  private val connection = mongo.connection(servers)
  val propertyDB = connection.db(db)
  lazy val propertyCollection: BSONCollection = propertyDB.collection(propertyCollectionName)
  lazy val checksumCollection: BSONCollection = propertyDB.collection(checksumCollectionName)
  lazy val actorSystem = mongo.system
  def shutdown() : Unit = { mongo close }
}
