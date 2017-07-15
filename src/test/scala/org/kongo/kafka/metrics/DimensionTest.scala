package org.kongo.kafka.metrics

import java.util.Collections

import org.junit.Assert
import org.junit.Test
import org.kongo.kafka.metrics.config.PropertiesMapBehavior

class DimensionTest {

  @Test
  def defaultDimensionsFromMapConfig(): Unit = {
    val config = TestUtils.emptyMapConfigBehavior
    val dims = Dimension.fromConfig(config, "")

    Assert.assertEquals(Dimension.Values, dims)
  }

  @Test
  def defaultDimensionsFromVerifiableProperties(): Unit = {
    val config = TestUtils.emptyVerifiableConfigBehavior
    val dims = Dimension.fromConfig(config, "")

    Assert.assertEquals(Dimension.Values, dims)
  }

  @Test
  def dimensionFromMapConfig(): Unit = {
    val config = new PropertiesMapBehavior(Collections.singletonMap("p999", false))
    val dims = Dimension.fromConfig(config, "")
    val expected = Dimension.Values - Dimension.Percentile999

    Assert.assertEquals(expected, dims)
  }

  @Test
  def dimensionWithPrefixFromMapConfig(): Unit = {
    val config = new PropertiesMapBehavior(Collections.singletonMap("external.dimension.p999", false))
    val dims = Dimension.fromConfig(config, "external.dimension")
    val expected = Dimension.Values - Dimension.Percentile999

    Assert.assertEquals(expected, dims)
  }

  @Test
  def dimensionFromVerifiableProperties(): Unit = {
    val config = TestUtils.singletonVerifiablePropertiesBehavior("p999", "false")
    val dims = Dimension.fromConfig(config, "")
    val expected = Dimension.Values - Dimension.Percentile999

    Assert.assertEquals(expected, dims)
  }

  @Test
  def dimensionWithPrefixFromVerifiableProperties(): Unit = {
    val config = TestUtils.singletonVerifiablePropertiesBehavior("external.dimension.p999", "false")
    val dims = Dimension.fromConfig(config, "external.dimension")
    val expected = Dimension.Values - Dimension.Percentile999

    Assert.assertEquals(expected, dims)
  }
}
