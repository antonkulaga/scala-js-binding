package org.denigma.controls.sockets

import java.nio.ByteBuffer
import rx.Ctx.Owner.Unsafe.Unsafe

import boopickle.DefaultBasic._
import rx.Var
import org.denigma.binding.extensions._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait WebSocketTransport1 extends WebSocketTransport2 {
  type Output = Input
  def emptyOutput: Output = emptyInput
}

trait WebSocketTransport2 extends WebSocketSubscriber with BinaryWebSocket {

  type Input
  type Output

  def emptyInput: Input
  def emptyOutput: Output

  val input = Var(emptyInput)
  val output = Var(emptyOutput)


  def open(): Unit

  onMessage.triggerLater{
    val mess = onMessage.now
    println("message received "+mess)
    onMessage(mess)
  }

  protected def unpickle(bytes: ByteBuffer): Input

  protected def pickle(message: Output): ByteBuffer

  def ask[Result](message: Output, timeout: FiniteDuration)(partial: PartialFunction[Input, Result]): Future[Result] = {
    println("ask is used for message "+message)
    val expectation = TimeoutExpectation[Input, Result](input, timeout)(partial)
    output() = message
    expectation.future
  }

  def ask[Result](message: Output, timeout: FiniteDuration, retry: Int)(partial: PartialFunction[Input, Result]): Future[Result] = {
    def resend() = output() = message
    val expectation = RetryExpectation[Input, Result](input, resend, timeout, retry)(partial)
    resend()
    expectation.future
  }

  override protected def updateFromBinaryMessage(bytes: ByteBuffer): Unit = {
    input() = unpickle(bytes)
  }


  output.triggerLater{
    println("output trigger later")
    send(output.now)
  }

  def send(message: Output): Unit = if(opened.now) {
    println("send message: "+message)
    val mes = bytes2message(pickle(message))
    send(mes)
  } else opened.triggerOnce{
    case true =>
      println("send after delay")
      send(message)
    case false =>
  }

}
