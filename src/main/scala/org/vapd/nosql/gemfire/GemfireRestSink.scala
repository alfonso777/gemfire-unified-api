package org.vapd.nosql.gemfire

import com.ning.http.client.{AsyncHttpClient, Response, AsyncCompletionHandler}
import concurrent.{Promise, Future}
import scala.concurrent.Await
import scala.concurrent.duration._
import java.net.URLEncoder
import scala.util.parsing.json.JSON

object HttpCLient {
  val client = new AsyncHttpClient()
}

case class GemfireRestSink(region: String, hostname: String, port: Int) extends GemfireSink {

  val URL_BASE = s"http://$hostname:$port/gemfire-api/v1"
  val STATUS_OK = 200
  //val client = new AsyncHttpClient()
  def get(key: String): Option[String]  = {
    val url_get = s"""$URL_BASE/${region}/${URLEncoder.encode(key,"UTF-8")}"""
    val request = HttpCLient.client.prepareGet(url_get.trim())
    val response = request.execute().get
   
    if(response.getStatusCode == STATUS_OK)
      Some(getValue(response.getResponseBody))
    else None
  }

  def save(key: String, value: String): String = {
    val url_post = s"""$URL_BASE/$region?key=${URLEncoder.encode(key,"UTF-8")}"""
    val body: String = s"""{ "$KEY_NAME": "${value.replaceAll("\"","\\\\\"")}" }"""
    println(s"body: [${body}]")  
    val request = HttpCLient.client.preparePost(url_post)
          .addHeader("Content-Type", "application/json")
          .addHeader("Accept", "application/json")
          .setBody(body)
    val response = request.execute().get
    response.getResponseBody
  }

  def remove(key: String): String = {
    val url = s"""$URL_BASE/$region/${URLEncoder.encode(key,"UTF-8")}"""
    val request = HttpCLient.client.prepareDelete(url.trim())
    val response = Await.result(executeRequest(request), 3 second)
    response.getStatusCode.toString
  }

  def getValue(result: String) = {
    JSON.parseFull(result) match {
      case Some(map) => map.asInstanceOf[Map[String, String]](KEY_NAME)
      case _ =>
        if( result.startsWith("\"") && result.endsWith("\"") )
          result.init.tail //handling the case when the value was saved via GemfireSocketSink
        else
          result
    }
  }

  private def executeRequest(preparedRequest: AsyncHttpClient#BoundRequestBuilder) = {
    val promise = Promise[Response]()
    preparedRequest.execute(new AsyncCompletionHandler[Response] {
        def onCompleted(response: Response): Response = { promise.success(response); response }
        override def onThrowable(t: Throwable ) { promise.failure(t) }
    })
    promise.future
  }
}
