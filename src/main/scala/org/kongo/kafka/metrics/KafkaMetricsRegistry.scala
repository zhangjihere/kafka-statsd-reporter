package org.kongo.kafka.metrics

import com.yammer.metrics.Metrics
import com.yammer.metrics.core.MetricsRegistry
import org.apache.kafka.common.Metric

/**
  * Basic wrapper class that encapsulates both the
  * yammer metrics registry ([[com.yammer.metrics.core.MetricsRegistry]])
  * and kafka's internal metrics into one handy object.
  *
  * @param registry yammer registry instance to use
  */
class KafkaMetricsRegistry(val registry: MetricsRegistry) {
  private val sync = new Object
  private var _metrics = Map.empty[String, Metric]

  /** Current map of kafka's metrics */
  def metrics: Map[String, Metric] = _metrics

  /** Update/insert an updated metric */
  def update(metric: Metric): Unit = {
    val key = MetricFormatter.format(metric)

    sync synchronized {
      _metrics = _metrics + (key -> metric)
    }
  }

  /** Remove a metric */
  def remove(metric: Metric): Unit = {
    val key = MetricFormatter.format(metric)

    sync synchronized {
      _metrics = _metrics - key
    }
  }
}

object KafkaMetricsRegistry {
  def apply(): KafkaMetricsRegistry =
    new KafkaMetricsRegistry(Metrics.defaultRegistry())
}
