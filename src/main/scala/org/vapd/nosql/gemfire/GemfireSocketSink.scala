package org.vapd.nosql.gemfire

import com.gemstone.gemfire.cache.{ Region, RegionExistsException }
import com.gemstone.gemfire.cache.client.{ClientCache, ClientCacheFactory, ClientRegionShortcut}
import com.gemstone.gemfire.cache.query.{SelectResults, Struct}
import com.gemstone.gemfire.pdx.PdxInstance

class GemfireSocketSink(regionName: String, host: String, port: Int)
  (
    clientCacheCreator: (String, Int) => ClientCache,
    regionCreator: (String, ClientCache) => Region[String, Object]
  ) extends GemfireSink {

  lazy val cache: ClientCache = clientCacheCreator(host, port)
  lazy val region: Region[String, Object] = regionCreator(regionName, cache)

  def get(key: String): Option[String] = {
      Option(region.get(key)) match {
        case Some(result: PdxInstance) =>Option(result.getField(KEY_NAME).toString)
        case Some(result) => Option(result.toString)
        case _ => None
      }
  }

  def save(key: String, value: String): String = {
    Option(region.put(key, value)).map(_.toString).getOrElse("")
  }

  def remove(key: String): String = {
    Option(region.remove(key)).map(_.toString).getOrElse("")
  }
}


object GemfireSocketSink {
  val clientCacheCreator: (String, Int) => ClientCache = (host: String, port: Int) => {
    new ClientCacheFactory()
      .addPoolLocator(host, port)
      .create()
  }

  val regionCreator: (String, ClientCache) => Region[String, Object] = (region: String, cache: ClientCache) => {
    sys.addShutdownHook(cache.close())
    try{
      cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create(region)
    } catch {
      case ex: RegionExistsException => cache.getRegion(region)
    }
  }

  def build(region: String, host: String, port: Int): GemfireSocketSink = {
    new GemfireSocketSink(region, host, port)(clientCacheCreator, regionCreator)
  }

}
