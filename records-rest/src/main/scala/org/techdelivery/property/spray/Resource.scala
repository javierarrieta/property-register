package org.techdelivery.property.spray

import java.net.URLDecoder

case class Resource(path:List[String],query:Map[String,Option[String]])

object Resource {
  def apply(uri: String): Resource = {
    val (path,query) = uri.split("""\?""").toList match {
      case x :: Nil => (x,None)
      case x :: xs => (x, xs mkString "?")
    }

    val pathSegments = path.split("/").filter { s=> s.length > 0 }
    val queryMap: Map[String, Option[String]] = query match {
      case q: String => q.split("""\&""").toList.map { s =>
        s.split("=").toList match {
          case x :: Nil => (x,None)
          case x :: xs => (x, Some( URLDecoder.decode( xs.mkString("="), "UTF-8")))
        }
      } toMap
      case None => Map()
    }
    new Resource(pathSegments.toList, queryMap)
  }
}