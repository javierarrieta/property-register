package org.techdelivery.property.spray

import spray.can.server.SprayCanHttpServerApp
import akka.actor.Props
import spray.io.PipelineContext
import akka.actor.ActorRef
import spray.can.server.HttpServer
import spray.io.IOExtension
import spray.io.PerConnectionHandler
import spray.can.server.ServerSettings
import org.techdelivery.property.settings.configuration.conf
import com.typesafe.scalalogging.slf4j.Logging
import org.techdelivery.property.mongo.propertyMongoDB


object PropertyRegisterApp extends App with SprayCanHttpServerApp with Logging {
  
  implicit val ioBridge = IOExtension(system).ioBridge()
  
  def messageCreator(ctx:PipelineContext) : ActorRef = {
    system.actorOf(Props( new PropertyResource(propertyMongoDB.propertyCollection)))
  }
  
  
  val httpServer = system.actorOf(Props(new HttpServer(ioBridge, PerConnectionHandler(messageCreator), ServerSettings())), "http-server")
  // create a new HttpServer using our handler and tell it where to bind to
  httpServer ! Bind(interface = conf.getString("http.server.interface"), port = conf.getInt("http.server.port"))
}