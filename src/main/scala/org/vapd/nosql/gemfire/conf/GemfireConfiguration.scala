package org.vapd.nosql.gemfire.conf

import com.typesafe.config.{Config, ConfigFactory}

case class Connection(kind: String, host: String, port: String) extends Serializable 

case class GemfireConfiguration(region: String, connection: Connection) extends Serializable

object GemfireConfiguration {
  def buildFromConfiguration(config: Config) = {
    val region = config.getString("gemfire.region")
    val host = config.getString("gemfire.connection.host")
    val port = config.getString("gemfire.connection.port")
    val kindConnection = config.getBoolean("gemfire.connection.type")
    if(kindConnection equals "socket")
      GemfireConfiguration(region, Connection("socket", host, port))
    else if(kindConnection equals "rest")
      GemfireConfiguration(region, Connection("rest", host, port))
    else 
      throw new IllegalArgumentException("Connection type not supported")
  }
}
