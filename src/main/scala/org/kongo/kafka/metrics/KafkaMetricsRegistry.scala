package org.kongo.kafka.metrics

import com.yammer.metrics.Metrics
import com.yammer.metrics.core.MetricsRegistry
import org.apache.kafka.common.metrics.KafkaMetric

/**
  * Basic wrapper class that encapsulates both the
  * yammer metrics registry ([[com.yammer.metrics.core.MetricsRegistry]])
  * and kafka's internal metrics into one handy object.
  *
  * @param registry yammer registry instance to use
  */
class KafkaMetricsRegistry(val registry: MetricsRegistry) {
  private val sync = new Object
  private var _metrics = Map.empty[String, KafkaMetric]

  /** Current map of kafka's metrics */
  def metrics: Map[String, KafkaMetric] = _metrics

  /** Update/insert an updated metric */
  def update(metric: KafkaMetric): Unit = {
    val key = MetricFormatter.format(metric)

    sync synchronized {
      _metrics = _metrics + (key -> metric)
    }
  }

  /** Remove a metric */
  def remove(metric: KafkaMetric): Unit = {
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
