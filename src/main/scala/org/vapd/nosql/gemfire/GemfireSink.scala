package org.vapd.nosql.gemfire

import org.vapd.nosql.gemfire.conf._

trait GemfireSink  extends Serializable{
  val KEY_NAME = "value"

  def get(key: String): Option[String]

  def save(key: String, value: String): String

  def remove(key: String): String
}

object GemfireSink {
  def createGemfireSink(config: GemfireConfiguration) = config.connection.kind match {
    case ConnectionType.SOCKET => GemfireSocketSink.build(config.region, config.connection.host, config.connection.port)
    case ConnectionType.REST   => new GemfireRestSink(config.region, config.connection.host, config.connection.port)
    case _ => throw new IllegalArgumentException("Connection type not supported!")
  }
}
