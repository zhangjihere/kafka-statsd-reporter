package org.kongo.kafka.metrics

import java.util.function.BiConsumer

import com.timgroup.statsd.NonBlockingStatsDClient
import com.yammer.metrics.core.Counter
import com.yammer.metrics.core.Gauge
import com.yammer.metrics.core.Histogram
import com.yammer.metrics.core.Metered
import com.yammer.metrics.core.Metric
import com.yammer.metrics.core.MetricName
import com.yammer.metrics.core.MetricProcessor
import com.yammer.metrics.core.MetricsRegistry
import com.yammer.metrics.core.Sampling
import com.yammer.metrics.core.Summarizable
import com.yammer.metrics.core.Timer
import com.yammer.metrics.reporting.AbstractPollingReporter
import kafka.utils.Logging

import scala.language.implicitConversions

private class YammerReporterThread(registry: MetricsRegistry, config: KafkaStatsdReporterConfig)
  extends AbstractPollingReporter(registry, KafkaStatsdReporter.Name) with MetricProcessor[Long] with Logging {

  private val statsd = new NonBlockingStatsDClient(config.prefix, config.host, config.port)

  override def run(): Unit = {
    val now = System.nanoTime()
    getMetricsRegistry.allMetrics().forEach { (name: MetricName, metric: Metric) =>
      if (metric != null && config.predicate.matches(name, metric))
        metric.processWith(this, name, now)
    }
  }

  override def shutdown(): Unit = {
    super.shutdown()
    statsd.stop()
  }

  override def processHistogram(name: MetricName, histogram: Histogram, time: Long): Unit = {
    processSum(format(name), histogram)
  }

  override def processCounter(name: MetricName, counter: Counter, time: Long): Unit = {
    statsd.gauge(format(name), counter.count())
  }

  override def processMeter(name: MetricName, meter: Metered, time: Long): Unit = {
    processMetered(format(name), meter)
  }

  override def processTimer(name: MetricName, timer: Timer, time: Long): Unit = {
    val formatted = format(name)

    processSum(formatted, timer)
    processMetered(formatted, timer)
    processSampling(formatted, timer)
  }

  override def processGauge(name: MetricName, gauge: Gauge[_], time: Long): Unit = {
    val formatted = format(name)

    gauge.value() match {
      case f: Float =>
        statsd.gauge(formatted, f)
      case d: Double =>
        statsd.gauge(formatted, d)
      case i: Int =>
        statsd.gauge(formatted, i)
      case l: Long =>
        statsd.gauge(formatted, l)
      case b: Byte =>
        statsd.gauge(formatted, b)
      case s: Short =>
        statsd.gauge(formatted, s)
      case bd: BigDecimal =>
        statsd.gauge(formatted, bd.doubleValue())
      case unsupported =>
        logger.warn(s"unsupported value type for Gauge: $unsupported [$formatted, ${ unsupported.getClass }]")
    }
  }

  private def format(name: MetricName): String = {
    RegexMetricPredicate.build(name)
  }

  private def sendGauge(name: String, dim: String, value: Double): Unit = {
    statsd.gauge(name + "." + dim, value)
  }

  private def sendGauge(name: String, dim: String, value: Long): Unit = {
    statsd.gauge(name + "." + dim, value)
  }

  private def processSum(name: String, sum: Summarizable): Unit = {
    sendGauge(name, "min", sum.min())
    sendGauge(name, "max", sum.max())
    sendGauge(name, "stddev", sum.stdDev())
    sendGauge(name, "mean", sum.mean())
    sendGauge(name, "sum", sum.sum())
  }

  private def processMetered(name: String, meter: Metered): Unit = {
    sendGauge(name, "count", meter.count())
    sendGauge(name, "meanRate", meter.meanRate())
    sendGauge(name, "rate1m", meter.oneMinuteRate())
    sendGauge(name, "rate5m", meter.fiveMinuteRate())
    sendGauge(name, "rate15m", meter.fifteenMinuteRate())
  }

  private def processSampling(name: String, sampling: Sampling): Unit = {
    val snapshot = sampling.getSnapshot

    sendGauge(name, "median", snapshot.getMedian)
    sendGauge(name, "p75", snapshot.get75thPercentile())
    sendGauge(name, "p95", snapshot.get95thPercentile())
    sendGauge(name, "p98", snapshot.get98thPercentile())
    sendGauge(name, "p99", snapshot.get99thPercentile())
    sendGauge(name, "p999", snapshot.get999thPercentile())
  }

  private implicit def func2BiConsumer[A, B](func: (A, B) => Unit): BiConsumer[A, B] = new BiConsumer[A, B] {
    override def accept(t: A, u: B): Unit = func.apply(t, u)
  }
}

