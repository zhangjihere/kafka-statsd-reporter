package org.kongo.kafka.metrics.config

import kafka.utils.VerifiableProperties

class VerifiableConfigBehavior(props: VerifiableProperties) extends MetricsConfigBehavior {
  override def getString(key: String, default: String): String =
    props.getString(key, default)

  override def getInt(key: String, default: Int): Int =
    props.getInt(key, default)

  override def getBoolean(key: String, default: Boolean): Boolean =
    props.getBoolean(key, default)

  override def contains(key: String): Boolean =
    props.containsKey(key)
}
