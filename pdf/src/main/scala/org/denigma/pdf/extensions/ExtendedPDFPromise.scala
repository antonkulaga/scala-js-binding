package org.denigma.pdf.extensions

import org.denigma.pdf.PDFPromise

import scala.concurrent.Promise
import scalajs.concurrent.JSExecutionContext.Implicits.queue
/**
  * Created by antonkulaga on 6/3/16.
  */
class ExtendedPDFPromise[T](val promisePDF: PDFPromise[T]) extends AnyVal {

  def toFuture = {

    val p = Promise[T]
    def onResolve(value: T): Unit = p.success(value)

    def onReject(message: Any): Unit = {
      println(s"any failed: $message")
      p.failure(new Exception(s"PDF promise exception: $message"))
    }
    promisePDF.then(onResolve _, onReject _)
    p.future
  }
}
