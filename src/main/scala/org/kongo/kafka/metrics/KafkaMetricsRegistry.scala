package org.kongo.kafka.metrics

import com.yammer.metrics.Metrics
import com.yammer.metrics.core.MetricsRegistry
import org.apache.kafka.common.metrics.KafkaMetric

class KafkaMetricsRegistry(val registry: MetricsRegistry) {
  private val sync = new Object
  private var _metrics = Map.empty[String, KafkaMetric]

  def metrics: Map[String, KafkaMetric] = _metrics

  def update(metric: KafkaMetric): Unit = {
    val key = MetricFormatter.format(metric)

    sync synchronized {
      _metrics = _metrics + (key -> metric)
    }
  }

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
