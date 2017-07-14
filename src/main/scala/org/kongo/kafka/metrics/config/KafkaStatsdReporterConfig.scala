package org.kongo.kafka.metrics.config

import java.util.regex.Pattern

import kafka.utils.VerifiableProperties
import org.kongo.kafka.metrics.Dimension
import org.kongo.kafka.metrics.RegexMetricPredicate

import scala.util.Try

/**
  * Class containing all config values that are available to configure the
  * StatsD metrics reporter. These are basically:
  *   - host
  *   - port
  *   - prefix
  *   - reporter.enabled
  *   - include
  *   - exclude
  *   - dimension.enabled.*
  *
  * @param behavior behavior that implements the config backend that is used
  */
class KafkaStatsdReporterConfig(behavior: MetricsConfigBehavior) {
  import KafkaStatsdReporterConfig._

  /** StatsD server host (default: localhost) */
  val host: String = behavior.getString(s"${ ConfigBase }.host", "localhost")

  /** StatsD server port (default: 8125) */
  val port: Int = behavior.getInt(s"${ ConfigBase }.port", 8125)

  /** string that is prefixed to each metric (default: kafka) */
  val prefix: String = behavior.getString(s"${ ConfigBase }.prefix", "kafka")

  /** whether the reporter is enabled at all (default: false) */
  val enabled: Boolean = behavior.getBoolean(s"${ ConfigBase }.reporter.enabled", false)

  /** whitelist regex pattern for metrics */
  val include: Option[Pattern] = pattern("include")

  /** blacklist regex pattern for metrics */
  val exclude: Option[Pattern] = pattern("exclude")

  /** [[org.kongo.kafka.metrics.RegexMetricPredicate]] that combines the configured
    * `include` and `exclude` patterns
    */
  val predicate: RegexMetricPredicate = RegexMetricPredicate(include, exclude)

  /** set of dimensions that should be exported for each gauge/histogram like metric */
  val dimensions: Set[Dimension] = Dimension.fromConfig(behavior, s"${ ConfigBase }.dimension.enabled")

  /** metrics polling interval */
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
