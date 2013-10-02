package org.techdelivery.property.spray

import akka.actor.{ActorLogging, Actor}
import reactivemongo.api.collections.default.BSONCollection
import nl.grons.metrics.scala.ReceiveTimerActor
import org.techdelivery.property.spray.metrics.Instrumented

trait PropertyResource extends Actor with PropertyServiceRoute with ActorLogging {

  def actorRefFactory = context

  override def receive = runRoute(route)

  def logger = log

}

class InstrumentedPropertyResource(collection: BSONCollection)  extends PropertyResource with ReceiveTimerActor with Instrumented {

  protected def mongoCollection = collection
}
