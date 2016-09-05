package org.denigma.binding.extensions
import org.scalajs.dom.raw.{Blob, Event, FileReader, ProgressEvent}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}
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

  implicit class BlobOpt(blob: Blob) {

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
      reader.readAsText(blob)
      result.future
    }


    def readAsByteBuffer: Future[ByteBuffer] = {
      val result = Promise[ByteBuffer]
      val reader = new FileReader()
      def onLoadEnd(ev: ProgressEvent): Any = {
        val buff = reader.result.asInstanceOf[ArrayBuffer]
        val bytes = TypedArrayBuffer.wrap(buff)
        result.success(bytes)
      }
      def onErrorEnd(ev: Event): Any = {
        result.failure(new Exception("READING FAILURE " + ev.toString))
      }
      reader.onloadend = onLoadEnd _
      reader.onerror = onErrorEnd _
      reader.readAsArrayBuffer(blob)
      result.future
    }

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
      reader.readAsArrayBuffer(blob)
      result.future
    }

  }
}
