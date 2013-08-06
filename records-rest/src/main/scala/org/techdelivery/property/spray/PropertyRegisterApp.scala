package org.techdelivery.property.spray

import akka.actor.{ActorSystem, Props}
import org.techdelivery.property.settings.configuration.conf
import com.typesafe.scalalogging.slf4j.Logging
import org.techdelivery.property.mongo.propertyMongoDB
import spray.can.Http
import akka.io.IO
import spray.can.Http.Bind
import com.typesafe.config.ConfigFactory

object http_config {
  private lazy val http_conf = ConfigFactory.load("rest-api.conf").withFallback(conf)
  lazy val http_iface = http_conf.getString("http.server.interface")
  lazy val http_port = http_conf.getInt("http.server.port")
}

object PropertyRegisterApp extends App with Logging {

  import http_config._

  implicit val system = ActorSystem()

  val property_resource = system.actorOf(Props( new PropertyResource(propertyMongoDB.propertyCollection)))
  // create a new HttpServer using our handler and tell it where to bind to
  IO(Http) ! Bind(property_resource, interface = http_iface, port = http_port)
}