package org.denigma.controls.selection


import java.nio.ByteBuffer

import org.scalajs.dom
import org.scalajs.dom.raw.{FileReader, MessageEvent}
import org.scalajs.dom.{Blob, ProgressEvent}

import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

object CloseCode extends Enumeration {
  val CLOSE_NORMAL = Value(1000)
  val CLOSE_GOING_AWAY = Value(1001)
  val CLOSE_PROTOCOL_ERROR = Value(1002)
  val CLOSE_UNSUPPORTED = Value(1003)
  val CLOSE_NO_STATUS = Value(1005)
  val CLOSE_ABNORMAL = Value(1006)
  val CLOSE_TOO_LARGE = Value(1009)
}

trait BinaryWebSocket {
  self=>

  protected def updateFromMessage(bytes: ByteBuffer): Unit

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
      case str: String =>  dom.console.log(s"message from websocket: " + str)

      case blob: Blob=>
        //println("blob received:"+blob)
        val reader = new FileReader()
        def onLoadEnd(ev: ProgressEvent): Any = {
          val buff = reader.result
          updateFromMessage(toByteBuffer(buff))
        }
        reader.onloadend = onLoadEnd _
        reader.readAsArrayBuffer(blob)

      case buff: ArrayBuffer=>
        val bytes = TypedArrayBuffer.wrap(buff)
        updateFromMessage(bytes)
    }
  }

}
