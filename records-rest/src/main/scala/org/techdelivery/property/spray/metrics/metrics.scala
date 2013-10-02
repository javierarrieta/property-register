package org.techdelivery.property.spray.metrics

import com.codahale.metrics.{JmxReporter, MetricRegistry}
import nl.grons.metrics.scala.InstrumentedBuilder

object metricsContainer {
  lazy implicit val registry = new MetricRegistry
  val reporter = JmxReporter.forRegistry(registry).build()
  reporter start

}

trait Instrumented extends InstrumentedBuilder {
  val metricRegistry = metricsContainer.registry
}