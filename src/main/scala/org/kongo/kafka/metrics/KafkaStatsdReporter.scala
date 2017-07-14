package org.kongo.kafka.metrics

import java.util
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

import kafka.metrics.KafkaMetricsReporter
import kafka.utils.Logging
import kafka.utils.VerifiableProperties
import org.apache.kafka.common.metrics.KafkaMetric
import org.apache.kafka.common.metrics.MetricsReporter
import org.kongo.kafka.metrics.config.KafkaStatsdReporterConfig

import scala.collection.JavaConverters._

class KafkaStatsdReporter extends KafkaMetricsReporter with MetricsReporter with Logging {
  logger.info(s"initializing ${ KafkaStatsdReporter.Name }")

  private val running = new AtomicReference(false)
  private val registry = KafkaMetricsRegistry()

  private var config: KafkaStatsdReporterConfig = _
  private var underlying: YammerReporterThread = _

  override def init(props: VerifiableProperties): Unit = {
    config = KafkaStatsdReporterConfig(props)

    logger.info(s"init: built config $config")
  }

  def startReporter(pollingInterval: Long): Unit = this synchronized {
    if (running.get) {
      logger.warn(s"${ KafkaStatsdReporter.Name } is already running")
    } else if (!config.enabled) {
      logger.warn(s"${ KafkaStatsdReporter.Name } is not enabled - not starting now")
    } else {
      underlying = new YammerReporterThread(registry, config)
      underlying.start(config.pollingIntervalSecs, TimeUnit.SECONDS)

      running.set(true)
      logger.info(s"${ KafkaStatsdReporter.Name } is now running")
    }
  }

  def stopReporter(): Unit = this synchronized {
    if (running.get) {
      logger.info(s"stopped ${ KafkaStatsdReporter.Name }")

      // stop underlying thread
      try underlying.shutdown()
      catch {
        case ie: InterruptedException =>
          logger.warn("stop reporter exception", ie)
      }

      running.set(false)
      logger.info(s"stopped ${ KafkaStatsdReporter.Name } at ${ config.host }:${ config.port }")
    }
  }

  override def close(): Unit = {
    logger.trace("close")
    stopReporter()
  }

  override def init(metrics: util.List[KafkaMetric]): Unit = {
    logger.trace(s"init: $metrics")

    metrics.asScala.foreach(update)
    startReporter(config.pollingIntervalSecs)
  }

  override def metricRemoval(metric: KafkaMetric): Unit = {
    logger.trace(s"metricRemoval: ${ metric.metricName() }")
    registry.remove(metric)
  }

  override def metricChange(metric: KafkaMetric): Unit = {
    logger.trace(s"metricChange: ${ metric.metricName() }")
    update(metric)
  }

  override def configure(configs: util.Map[String, _]): Unit = {
    logger.trace(s"configure: $configs")

    config = KafkaStatsdReporterConfig(configs)
    logger.info(s"configured to ${ config }")
  }

  private def update(metric: KafkaMetric): Unit = {
    // we can filter the metric already at this point before even
    // recording them in our internal registry
    if (config.predicate.matches(MetricFormatter.format(metric)))
      registry.update(metric)
  }
}

object KafkaStatsdReporter {
  val Name = "kafka-statsd-reporter"
}
