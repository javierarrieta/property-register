package org.techdelivery.property.spray.metrics

import com.codahale.metrics.{JmxReporter, MetricRegistry}
import nl.grons.metrics.scala.InstrumentedBuilder

object metrics {
  lazy implicit val registry = new MetricRegistry
}

trait Instrumented extends InstrumentedBuilder {
  val metricRegistry = metrics.registry
  val reporter = JmxReporter.forRegistry(metricRegistry).build()
}