package org.denigma.controls.sockets

import java.nio.ByteBuffer

import org.scalajs.dom
import org.scalajs.dom.raw.{Blob, FileReader, MessageEvent, ProgressEvent}

import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

trait BinaryWebSocket {

  protected def updateFromBinaryMessage(bytes: ByteBuffer): Unit

  implicit def bytes2message(data: ByteBuffer): ArrayBuffer = {
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

  protected def toByteBuffer(data: Any) = TypedArrayBuffer.wrap(data.asInstanceOf[ArrayBuffer])

  protected def onMessage(mess: MessageEvent) = {
    mess.data match{
      case str: String=>  dom.console.log(s"message from websocket: " + str)

      case blob: Blob=>
        val reader = new FileReader()
        def onLoadEnd(ev: ProgressEvent): Any = {
          val buff = reader.result
          updateFromBinaryMessage(toByteBuffer(buff))
        }
        reader.onloadend = onLoadEnd _
        reader.readAsArrayBuffer(blob)

      case buff: ArrayBuffer=>
        val bytes: ByteBuffer = TypedArrayBuffer.wrap(buff)
        updateFromBinaryMessage(bytes)
    }
  }

}
