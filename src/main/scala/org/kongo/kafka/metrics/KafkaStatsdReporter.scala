package org.kongo.kafka.metrics

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

import com.yammer.metrics.Metrics
import kafka.metrics.KafkaMetricsReporter
import kafka.utils.Logging
import kafka.utils.VerifiableProperties

class KafkaStatsdReporter extends KafkaMetricsReporter with Logging {
  private val running = new AtomicReference(false)

  private var config: KafkaStatsdReporterConfig = _
  private var underlying: YammerReporterThread = _

  override def init(props: VerifiableProperties): Unit = {
    config = new KafkaStatsdReporterConfig(props)

    logger.info(s"init: built config $config")
  }

  def startReporter(pollingInterval: Int): Unit = this synchronized {
    if (running.get) {
      logger.warn(s"${ KafkaStatsdReporter.Name } is already running")
    } else if (!config.enabled) {
      logger.warn(s"${ KafkaStatsdReporter.Name } is not enabled - not starting now")
    } else {
      underlying = new YammerReporterThread(Metrics.defaultRegistry(), config)
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

}

object KafkaStatsdReporter {
  val Name = "kafka-statsd-reporter"
}
