package org.kongo.kafka.metrics

import java.util.regex.Pattern

import kafka.metrics.KafkaMetricsConfig
import kafka.utils.VerifiableProperties

import scala.util.Try

class KafkaStatsdReporterConfig(props: VerifiableProperties) extends KafkaMetricsConfig(props) {
  import KafkaStatsdReporterConfig._

  val host: String = props.getString(s"${ ConfigBase }.host", "localhost")

  val port: Int = props.getInt(s"${ ConfigBase }.port", 8125)

  val prefix: String = props.getString(s"${ ConfigBase }.prefix", "kafka")

  val enabled: Boolean = props.getBoolean(s"${ ConfigBase }.reporter.enabled", false)

  val include: Option[Pattern] = pattern("include")

  val exclude: Option[Pattern] = pattern("exclude")

  val predicate: RegexMetricPredicate = RegexMetricPredicate(include, exclude)

  private def pattern(key: String): Option[Pattern] = {
    val propsKey = s"${ ConfigBase }.${ key }"
    if (props.containsKey(propsKey))
      Try(Pattern.compile(props.getString(propsKey))).toOption
    else
      None
  }
}

object KafkaStatsdReporterConfig {
  val ConfigBase = "kafka.statsd.metrics"
}
