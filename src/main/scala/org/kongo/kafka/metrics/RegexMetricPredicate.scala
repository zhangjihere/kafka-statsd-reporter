package org.kongo.kafka.metrics

import java.util.regex.Pattern

import com.yammer.metrics.core.Metric
import com.yammer.metrics.core.MetricName
import com.yammer.metrics.core.MetricPredicate

case class RegexMetricPredicate(include: Option[Pattern], exclude: Option[Pattern]) extends MetricPredicate {
  def matches(metricName: String): Boolean = {
    val includeMatch = include.forall(p => p.matcher(metricName).matches())
    val notExcludeMatch = exclude.forall(p => !p.matcher(metricName).matches())

    includeMatch && notExcludeMatch
  }

  override def matches(name: MetricName, metric: Metric): Boolean = {
    val metricName = MetricFormatter.format(name)
    matches(metricName)
  }
}

