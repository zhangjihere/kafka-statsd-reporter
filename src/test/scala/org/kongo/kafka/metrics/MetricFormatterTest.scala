package org.kongo.kafka.metrics

import com.yammer.metrics.core.MetricName
import org.junit.Assert.assertEquals
import org.junit.Test

class MetricFormatterTest {

  @Test
  def yammerMetricFormatting(): Unit = {
    val metric = new MetricName("group", "type", "name")
    val formatted = MetricFormatter.format(metric)

    assertEquals("group.type.name", formatted)
  }

  @Test
  def yammerMetricWithScopeFormatting(): Unit = {
    val metric = new MetricName("group", "type", "name", "scope")
    val formatted = MetricFormatter.format(metric)

    assertEquals("group.type.scope.name", formatted)
  }

  @Test
  def kafkaMetricFormatting(): Unit = {
    val metric = TestUtils.dummyKafkaMetric
    val formatted = MetricFormatter.format(metric)

    assertEquals("kafka.group.name", formatted)
  }

}
