package org.kongo.kafka.metrics

import java.text.NumberFormat
import java.util
import java.util.Locale
import java.util.function.BiConsumer

import com.timgroup.statsd.NonBlockingStatsDClient
import com.yammer.metrics.core.{VirtualMachineMetrics, _}
import com.yammer.metrics.reporting.AbstractPollingReporter
import kafka.utils.Logging
import org.kongo.kafka.metrics.config.KafkaStatsdReporterConfig

import scala.language.implicitConversions

private class YammerReporterThread(registry: KafkaMetricsRegistry, config: KafkaStatsdReporterConfig)
  extends AbstractPollingReporter(registry.registry, KafkaStatsdReporter.Name) with MetricProcessor[Long] with Logging {

  logger.info(s"initializing yammer reporter thread - statsd client connecting to ${ statsdHost }")

  private val statsd = new NonBlockingStatsDClient(config.prefix, config.host, config.port)

  private val vm = VirtualMachineMetrics.getInstance()

  override def run(): Unit = {
    val now = System.nanoTime()

    // process yammer metrics first
    getMetricsRegistry.allMetrics().forEach { (name: MetricName, metric: Metric) =>
      if (metric != null && config.predicate.matches(name, metric))
        metric.processWith(this, name, now)
    }

    // process kafka's metrics now
    registry.metrics.foreach { case (key, metric) =>
      statsd.gauge(key, metric.value())
    }
    statsd.gauge("jvm.heap_init", vm.heapInit())
    statsd.gauge("jvm.heap_used", vm.heapUsed())
    statsd.gauge("jvm.heap_max", vm.heapMax())
    statsd.gauge("jvm.heap_usage", vm.heapUsage())
    statsd.gauge("jvm.total_init", vm.totalInit())
    statsd.gauge("jvm.total_used", vm.totalUsed())
    statsd.gauge("jvm.total_max", vm.totalMax())
  }

  override def shutdown(): Unit = {
    super.shutdown()

    logger.info(s"stopping statsd client at ${ statsdHost }")
    statsd.stop()
  }

  override def processHistogram(name: MetricName, histogram: Histogram, time: Long): Unit = {
    processSum(MetricFormatter.format(name), histogram)
  }

  override def processCounter(name: MetricName, counter: Counter, time: Long): Unit = {
    statsd.gauge(MetricFormatter.format(name), counter.count())
  }

  override def processMeter(name: MetricName, meter: Metered, time: Long): Unit = {
    processMetered(MetricFormatter.format(name), meter)
  }

  override def processTimer(name: MetricName, timer: Timer, time: Long): Unit = {
    val formatted = MetricFormatter.format(name)

    processSum(formatted, timer)
    processMetered(formatted, timer)
    processSampling(formatted, timer)
  }

  override def processGauge(name: MetricName, gauge: Gauge[_], time: Long): Unit = {
    val formatted = MetricFormatter.format(name)

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
        logger.debug(s"unsupported value type for Gauge: $unsupported [$formatted, ${ unsupported.getClass }]")
    }
  }

  private def sendGauge(name: String, dim: Dimension, value: Double): Unit = {
    if (config.dimensions.contains(dim))
      statsd.gauge(name + "." + dim.name, value)
  }

  private def sendGauge(name: String, dim: Dimension, value: Long): Unit = {
    if (config.dimensions.contains(dim))
      statsd.gauge(name + "." + dim.name, value)
  }

  private def processSum(name: String, sum: Summarizable): Unit = {
    sendGauge(name, Dimension.Min, sum.min())
    sendGauge(name, Dimension.Max, sum.max())
    sendGauge(name, Dimension.StdDev, sum.stdDev())
    sendGauge(name, Dimension.Mean, sum.mean())
    sendGauge(name, Dimension.Sum, sum.sum())
  }

  private def processMetered(name: String, meter: Metered): Unit = {
    sendGauge(name, Dimension.Count, meter.count())
    sendGauge(name, Dimension.MeanRate, meter.meanRate())
    sendGauge(name, Dimension.Rate1Min, meter.oneMinuteRate())
    sendGauge(name, Dimension.Rate5Min, meter.fiveMinuteRate())
    sendGauge(name, Dimension.Rate15Min, meter.fifteenMinuteRate())
  }

  private def processSampling(name: String, sampling: Sampling): Unit = {
    val snapshot = sampling.getSnapshot

    sendGauge(name, Dimension.Median, snapshot.getMedian)
    sendGauge(name, Dimension.Percentile75, snapshot.get75thPercentile())
    sendGauge(name, Dimension.Percentile95, snapshot.get95thPercentile())
    sendGauge(name, Dimension.Percentile98, snapshot.get98thPercentile())
    sendGauge(name, Dimension.Percentile99, snapshot.get99thPercentile())
    sendGauge(name, Dimension.Percentile999, snapshot.get999thPercentile())
  }

  private def statsdHost = s"${ config.host }:${ config.port }"

  private implicit def func2BiConsumer[A, B](func: (A, B) => Unit): BiConsumer[A, B] = new BiConsumer[A, B] {
    override def accept(t: A, u: B): Unit = func.apply(t, u)
  }
}

