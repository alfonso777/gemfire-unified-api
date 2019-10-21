package org.vapd.nosql.gemfire.mock

import org.vapd.nosql.gemfire.GemfireSink

class MockGemfireSink extends GemfireSink {
  var region = Map[String, String]()

  def get(key: String) = region.lift(key)

  def save(key: String, value: String) = {region = region + (key -> value); ""}

  def remove(key: String) = { region = region - key; "" }

  def getRegion = region
}