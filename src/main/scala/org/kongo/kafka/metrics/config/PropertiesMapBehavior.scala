package org.kongo.kafka.metrics.config

class PropertiesMapBehavior(map: java.util.Map[String, _]) extends MetricsConfigBehavior {
  import PropertiesMapBehavior._

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
      case IntString(value) => value
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

object PropertiesMapBehavior {
  object IntString {
    def unapply(input: String): Option[Int] = {
      try Some(input.toInt)
      catch {
        case _: NumberFormatException => None
      }
    }
  }
}
