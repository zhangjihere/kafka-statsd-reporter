package org.kongo.kafka.metrics.config

trait MetricsConfigBehavior {
  def getString(key: String, default: String): String
  def getInt(key: String, default: Int): Int
  def getBoolean(key: String, default: Boolean): Boolean
  def contains(key: String): Boolean
}
