package org.kongo.kafka.metrics

import org.kongo.kafka.metrics.config.MetricsConfigBehavior

sealed trait Dimension {
  def name: String
}

object Dimension {
  // metered
  case object Count extends Dimension { override val name = "count" }
  case object MeanRate extends Dimension { override val name = "meanRate" }
  case object Rate1Min extends Dimension { override val name = "rate1m" }
  case object Rate5Min extends Dimension { override val name = "rate5m" }
  case object Rate15Min extends Dimension { override val name = "rate15m" }

  // summarizable
  case object Min extends Dimension { override val name = "min" }
  case object Max extends Dimension { override val name = "max" }
  case object Mean extends Dimension { override val name = "mean" }
  case object StdDev extends Dimension { override val name = "stddev" }
  case object Sum extends Dimension { override val name = "sum" }

  // sampling
  case object Median extends Dimension { override val name = "median" }
  case object Percentile75 extends Dimension { override val name = "p75" }
  case object Percentile95 extends Dimension { override val name = "p95" }
  case object Percentile98 extends Dimension { override val name = "p98" }
  case object Percentile99 extends Dimension { override val name = "p99" }
  case object Percentile999 extends Dimension { override val name = "p999" }

  /** All supported dimensions */
  val Values: Set[Dimension] = Set(
    Count,
    MeanRate,
    Rate1Min,
    Rate5Min,
    Rate15Min,
    Min,
    Max,
    Mean,
    StdDev,
    Sum,
    Median,
    Percentile75,
    Percentile95,
    Percentile98,
    Percentile99,
    Percentile999
  )

  /** Build a set of selected [[org.kongo.kafka.metrics.Dimension]] based on
    * the given configuration.
    * @param config configuration to use
    * @param prefix configuration prefix that is used
    * @return a set of [[org.kongo.kafka.metrics.Dimension]]
    */
  def fromConfig(config: MetricsConfigBehavior, prefix: String): Set[Dimension] =
    Values.filter(dim => config.getBoolean(s"${ prefix }.${ dim.name }", true))
}
