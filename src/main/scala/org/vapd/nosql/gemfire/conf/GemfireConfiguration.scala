package org.vapd.nosql.gemfire.conf

import com.typesafe.config.{Config, ConfigFactory}

object ConnectionType extends Enumeration {
  type ConnectionType = Value
  val SOCKET, REST, UNKNOWN = Value

  def withNameWithDefault(name: String): Value = 
    values.find(_.toString.toLowerCase equals name.toLowerCase).getOrElse(ConnectionType.UNKNOWN)
}

case class Connection(kind: ConnectionType.Value, host: String, port: Int) extends Serializable 

case class GemfireConfiguration(region: String, connection: Connection) extends Serializable

object GemfireConfiguration {
  def buildFromConfiguration(config: Config) = {
    val region = config.getString("gemfire.region")
    val host = config.getString("gemfire.connection.host")
    val port = config.getInt("gemfire.connection.port")
    val connectionType = ConnectionType.withNameWithDefault(config.getString("gemfire.connection.type"))
    GemfireConfiguration(region, Connection(connectionType, host, port))
  }
}
