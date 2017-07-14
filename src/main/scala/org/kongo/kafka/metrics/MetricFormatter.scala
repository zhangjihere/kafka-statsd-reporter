package org.kongo.kafka.metrics

import com.yammer.metrics.core.MetricName
import org.apache.kafka.common.metrics.KafkaMetric

object MetricFormatter {
  val KafkaPrefix = "kafka."

  def format(metric: KafkaMetric): String =
    KafkaPrefix + metric.metricName().group() + "." + metric.metricName().name()

  def format(name: MetricName): String = {
    val result = new StringBuilder().append(name.getGroup).append('.').append(name.getType).append('.')
    if (name.hasScope) {
      result.append(name.getScope).append('.')
    }
    result.append(name.getName).toString().replace(' ', '_')
  }
}
