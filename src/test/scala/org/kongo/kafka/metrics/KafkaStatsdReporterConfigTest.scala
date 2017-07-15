package org.kongo.kafka.metrics

import org.junit.Assert
import org.junit.Test
import org.kongo.kafka.metrics.config.KafkaStatsdReporterConfig

class KafkaStatsdReporterConfigTest {

  @Test
  def defaultMapConfig(): Unit = {
    val config = TestUtils.emptyMapConfig
    testDefaults(config)
  }

  @Test
  def defaultVerifiableProperties(): Unit = {
    val config = TestUtils.emptyVerfiableConfig
    testDefaults(config)
  }

  private def testDefaults(config: KafkaStatsdReporterConfig): Unit = {
    Assert.assertEquals("localhost", config.host)
    Assert.assertEquals(8125, config.port)
    Assert.assertEquals(10, config.pollingIntervalSecs)
    Assert.assertEquals(None, config.include)
    Assert.assertEquals(None, config.exclude)
    Assert.assertEquals(Dimension.Values, config.dimensions)
    Assert.assertEquals(false, config.enabled)
    Assert.assertEquals("kafka", config.prefix)
  }
}
