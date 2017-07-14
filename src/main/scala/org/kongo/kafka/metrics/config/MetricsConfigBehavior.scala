package org.kongo.kafka.metrics.config

/** Simple interface abstracting the basic functions of the
  * possible configuration backends (i.e. [[kafka.utils.VerifiableProperties]]
  * or [[java.util.Map]])
  */
trait MetricsConfigBehavior {
  /** Retrieve a string config value by key */
  def getString(key: String, default: String): String

  /** Retrieve an integer config value by key */
  def getInt(key: String, default: Int): Int

  /** Retrieve a boolean config value by key */
  def getBoolean(key: String, default: Boolean): Boolean

  /** Determine if a given key is present in the config */
  def contains(key: String): Boolean
}
