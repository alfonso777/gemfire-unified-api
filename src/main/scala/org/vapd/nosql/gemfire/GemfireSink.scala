package org.vapd.nosql.gemfire

import org.vapd.nosql.gemfire.conf._

trait GemfireSink  extends Serializable{
  def get(key: String): Option[String]

  def save(key: String, value: String): String

  def remove(key: String): String
}

object GemfireSink {
  def createGemfireSink(config: GemfireConfiguration) = ???
}
