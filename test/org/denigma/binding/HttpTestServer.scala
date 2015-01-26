package org.denigma.binding

import com.sun.net.httpserver._
import java.net.InetSocketAddress

/** serve the same static content for any request for test purposes */
class HttpTestServer(port: Int, val contentPath: String, var staticContent: String) {

  val server = HttpServer.create(new InetSocketAddress(port), 0)

  server.createContext(contentPath, new HttpHandler(){
    def handle(exchange: HttpExchange) {
      val bytes = staticContent.getBytes("UTF-8")
      exchange.sendResponseHeaders(200, bytes.length)
      val output = exchange.getResponseBody
      try {
        output.write(bytes)
      } finally {
        output.close()
      }
    }
  })
  server.start()

  def stop() {
    server.stop(0)
  }
}