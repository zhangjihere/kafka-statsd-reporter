package org.kongo.kafka.metrics.config

class PropertiesMapBehavior(map: java.util.Map[String, _]) extends MetricsConfigBehavior {
  override def getString(key: String, default: String): String = {
    map.get(key) match {
      case null => default
      case str: String => str
      case _ => default
    }
  }

  override def getInt(key: String, default: Int): Int = {
    map.get(key) match {
      case null => default
      case value: Int => value
      case _ => default
    }
  }

  override def getBoolean(key: String, default: Boolean): Boolean = {
    map.get(key) match {
      case null => default
      case b: Boolean => b
      case "false" => false
      case "true" => true
      case _ => default
    }
  }

  override def contains(key: String): Boolean =
    map.containsKey(key)
}
