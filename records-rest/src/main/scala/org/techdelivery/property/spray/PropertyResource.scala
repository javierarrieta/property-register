package org.techdelivery.property.spray

import akka.actor.{ActorLogging, Actor}
import reactivemongo.api.collections.default.BSONCollection

class PropertyResource
(collection: BSONCollection) extends Actor with PropertyServiceRoute with ActorLogging {

  def actorRefFactory = context

  def receive = runRoute(route)

  def logger = log

  protected def mongoCollection = collection

}
