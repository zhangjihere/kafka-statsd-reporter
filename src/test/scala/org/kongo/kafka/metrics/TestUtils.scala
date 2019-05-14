package org.kongo.kafka.metrics

import java.util.Collections
import java.util.Properties

import kafka.utils.VerifiableProperties
import org.apache.kafka.common.{MetricName => KMetricName}
import org.apache.kafka.common.Metric
import org.kongo.kafka.metrics.config.KafkaStatsdReporterConfig
import org.kongo.kafka.metrics.config.PropertiesMapBehavior
import org.kongo.kafka.metrics.config.VerifiableConfigBehavior

object TestUtils {

  def dummyKafkaMetric: Metric = {
    new Metric {
      override def metricName(): KMetricName = new KMetricName("name", "group", "description", Collections.emptyMap())
      override def value(): Double = 0d
    }
  }

  def singletonVerifiablePropertiesBehavior(key: String, value: AnyRef): VerifiableConfigBehavior =
    new VerifiableConfigBehavior(singletonVerifiableProperties(key, value))

  def singletonVerifiableProperties(key: String, value: AnyRef): VerifiableProperties = {
    val props = new Properties
    props.put(key, value)
    new VerifiableProperties(props)
  }

  def emptyVerfiableConfig: KafkaStatsdReporterConfig =
    KafkaStatsdReporterConfig(new VerifiableProperties())

  def emptyVerifiableConfigBehavior: VerifiableConfigBehavior = {
    val props = new VerifiableProperties()
    new VerifiableConfigBehavior(props)
  }

  def singletonMapConfigBehavior(key: String, value: String): PropertiesMapBehavior = {
    new PropertiesMapBehavior(Collections.singletonMap(key, value))
  }

  def emptyMapConfig: KafkaStatsdReporterConfig =
    KafkaStatsdReporterConfig(Collections.emptyMap[String, String]())

  def emptyMapConfigBehavior: PropertiesMapBehavior = {
    new PropertiesMapBehavior(Collections.emptyMap())
  }
}
