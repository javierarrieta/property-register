package org.techdelivery.property.spray

import akka.actor.{ActorSystem, Props}
import org.techdelivery.property.settings.configuration.conf
import com.typesafe.scalalogging.slf4j.Logging
import org.techdelivery.property.mongo.propertyMongoDB
import spray.can.Http
import akka.io.IO
import spray.can.Http.Bind


object PropertyRegisterApp extends App with Logging {

  implicit val system = ActorSystem()

  val property_resource = system.actorOf(Props( new PropertyResource(propertyMongoDB.propertyCollection)))
  // create a new HttpServer using our handler and tell it where to bind to
  IO(Http) ! Bind(property_resource, interface = conf.getString("http.server.interface"), port = conf.getInt("http.server.port"))
}