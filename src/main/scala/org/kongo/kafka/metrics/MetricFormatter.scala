package org.kongo.kafka.metrics

import com.yammer.metrics.core.MetricName
import org.apache.kafka.common.Metric

/**
  * Metrics formatting functions
  */
object MetricFormatter {
  val KafkaPrefix = "kafka."

  /** Build a metrics string based on a [[org.apache.kafka.common.Metric]] */
  def format(metric: Metric): String =
    KafkaPrefix + metric.metricName().group() + "." + metric.metricName().name()

  /** Build a metrics string based on a [[com.yammer.metrics.core.MetricName]] */
  def format(name: MetricName): String = {
    val result = new StringBuilder().append(name.getGroup).append('.').append(name.getType).append('.')
    if (name.hasScope) {
      result.append(name.getScope).append('.')
    }
    result.append(name.getName).toString().replace(' ', '_')
  }
}
