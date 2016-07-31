package org.denigma.binding.extensions
import org.scalajs.dom.raw.{Event, FileReader, ProgressEvent}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js.typedarray.ArrayBuffer
import scala.scalajs.js.typedarray.TypedArrayBufferOps._

trait DataOps {
  import java.nio.ByteBuffer

  implicit class ByteBufferOpt(data: ByteBuffer) {

    def toArrayBuffer: ArrayBuffer = {
      if (data.hasTypedArray()) {
        // get relevant part of the underlying typed array
        data.typedArray().subarray(data.position, data.limit).buffer
      } else {
        // fall back to copying the data
        val tempBuffer = ByteBuffer.allocateDirect(data.remaining)
        val origPosition = data.position
        tempBuffer.put(data)
        data.position(origPosition)
        tempBuffer.typedArray().buffer
      }
    }
  }

  implicit class FileOpt(f: org.scalajs.dom.File) {

    def readAsText: Future[String] = {
      val result = Promise[String]
      val reader = new FileReader()
      def onLoadEnd(ev: ProgressEvent): Any = {
        result.success(reader.result.toString)
      }
      def onErrorEnd(ev: Event): Any = {
        result.failure(new Exception("READING FAILURE " + ev.toString))
      }
      reader.onloadend = onLoadEnd _
      reader.onerror = onErrorEnd _
      reader.readAsText(f)
      result.future
    }
    /*

    def readAsBlob: Future[String] = {
      val result = Promise[String]
      val reader = new FileReader()
      def onLoadEnd(ev: ProgressEvent): Any = {
        result.success(reader.result.toString)
      }
      def onErrorEnd(ev: Event): Any = {
        result.failure(new Exception("READING FAILURE " + ev.toString))
      }
      reader.onloadend = onLoadEnd _
      reader.onerror = onErrorEnd _
      reader.readAsText(f)
      result.future
    }
    */


    def readAsArrayBuffer: Future[ArrayBuffer] = {
      val result = Promise[ArrayBuffer]
      val reader = new FileReader()
      def onLoadEnd(ev: ProgressEvent): Any = {
        result.success(reader.result.asInstanceOf[ArrayBuffer])
      }
      def onErrorEnd(ev: Event): Any = {
        result.failure(new Exception("READING FAILURE " + ev.toString))
      }
      reader.onloadend = onLoadEnd _
      reader.onerror = onErrorEnd _
      reader.readAsArrayBuffer(f)
      result.future
    }

  }
}
