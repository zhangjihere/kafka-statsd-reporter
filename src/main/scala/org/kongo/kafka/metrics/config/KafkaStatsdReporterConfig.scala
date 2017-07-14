package org.kongo.kafka.metrics.config

import java.util.regex.Pattern

import kafka.utils.VerifiableProperties
import org.kongo.kafka.metrics.Dimension
import org.kongo.kafka.metrics.RegexMetricPredicate

import scala.util.Try

class KafkaStatsdReporterConfig(behavior: MetricsConfigBehavior) {
  import KafkaStatsdReporterConfig._

  val host: String = behavior.getString(s"${ ConfigBase }.host", "localhost")

  val port: Int = behavior.getInt(s"${ ConfigBase }.port", 8125)

  val prefix: String = behavior.getString(s"${ ConfigBase }.prefix", "kafka")

  val enabled: Boolean = behavior.getBoolean(s"${ ConfigBase }.reporter.enabled", false)

  val include: Option[Pattern] = pattern("include")

  val exclude: Option[Pattern] = pattern("exclude")

  val predicate: RegexMetricPredicate = RegexMetricPredicate(include, exclude)

  val dimensions: Set[Dimension] = Dimension.fromConfig(behavior, s"${ ConfigBase }.dimension.enabled")

  val pollingIntervalSecs: Int = behavior.getInt("kafka.metrics.polling.interval.secs", 10)

  override def toString: String = {
    val dims = dimensions.map(_.name).mkString(",")
    s"[host=$host, port=$port, prefix=$prefix, enabled=$enabled, dimensions=($dims), polling-interval=$pollingIntervalSecs]"
  }

  private def pattern(key: String): Option[Pattern] = {
    val propsKey = s"${ ConfigBase }.${ key }"
    if (behavior.contains(propsKey))
      Try(Pattern.compile(behavior.getString(propsKey, null))).toOption
    else
      None
  }
}

object KafkaStatsdReporterConfig {
  val ConfigBase = "external.kafka.statsd.metrics"

  def apply(props: VerifiableProperties): KafkaStatsdReporterConfig =
    new KafkaStatsdReporterConfig(new VerifiableConfigBehavior(props))

  def apply(map: java.util.Map[String, _]): KafkaStatsdReporterConfig =
    new KafkaStatsdReporterConfig(new PropertiesMapBehavior(map))
}
