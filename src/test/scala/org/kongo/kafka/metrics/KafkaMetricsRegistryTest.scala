package org.kongo.kafka.metrics

import org.junit.Assert
import org.junit.Test

class KafkaMetricsRegistryTest {

  @Test
  def updateMetrics(): Unit = {
    val registry = KafkaMetricsRegistry()
    Assert.assertEquals(0, registry.metrics.size)

    registry.update(TestUtils.dummyKafkaMetric)
    Assert.assertEquals(1, registry.metrics.size)
  }

  @Test
  def removeMetrics(): Unit = {
    val metric = TestUtils.dummyKafkaMetric
    val registry = KafkaMetricsRegistry()

    registry.update(metric)
    Assert.assertEquals(1, registry.metrics.size)

    registry.remove(metric)
    Assert.assertEquals(0, registry.metrics.size)
  }
}
