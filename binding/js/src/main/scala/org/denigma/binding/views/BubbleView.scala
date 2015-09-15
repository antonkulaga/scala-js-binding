package org.denigma.binding.views

import scala.concurrent.{Promise, Future}

/**
 * View that can have bubbling events
 */
trait ViewEvent
{
  type Origin = BasicView
  val origin:Origin
  def latest:BasicView

  def withCurrent(cur:BasicView):ViewEvent
}

case class JustPromise[Value,Result](
                                      value:Value,
                                      origin:BasicView,
                                      latest:BasicView,
                                      promise:Promise[Result] = Promise[Result]()
                                      )
  extends PromiseEvent[Value,Result]
{
  override type Origin = BasicView

  override def withCurrent(cur:BasicView):JustPromise[Value,Result] = this.copy(latest = cur).asInstanceOf[this.type]
}

trait PromiseEvent[Value,Result] extends ViewEvent
{
  val value:Value
  val promise:Promise[Result]
  override def withCurrent(cur:BasicView):PromiseEvent[Value,Result]
}


trait BubbleView {
  self:OrganizedView=>
  /**
   * Event subsystem
   * @return
   */
  def receive:PartialFunction[ViewEvent,Unit] = {
    case event:ViewEvent=> this.propagate(event)
  }

  def receiveFuture:PartialFunction[PromiseEvent[_,_],Unit] = {
    case ev=>this.propagateFuture(ev)
  }


  /**
   * Propagates future to the top for some reason
   * @param event event that goes up
   * @return
   */
  protected def propagateFuture(event:PromiseEvent[_,_]) =
    this.fromParents{  case p:BubbleView=>p  } match {
      case Some(p)=> p.receiveFuture(event.withCurrent(this))
      case None=> event.promise.failure(new Exception(s"could not find value for ${event.origin}"))
    }

  /**
   * Fires an event
   * @param event
   * @param startWithMe
   */
  def fire(event:ViewEvent,startWithMe:Boolean = false): Unit = if(startWithMe) this.receive(event) else  this.propagate(event)

  protected def propagate(event:ViewEvent) =   this.fromParents{  case p:BubbleView=>p  } match {
    case Some(p)=> p.receive(event.withCurrent(this))
    case None=>
  }


  /**
   * Asks parent to provide some future
   * @param value value that we want
   * @param startWithMe
   * @tparam Value
   * @tparam Result result type that we get
   * @return
   */
  def ask[Value,Result](value:Value,startWithMe:Boolean = false):Future[Result] = {
    val event = new JustPromise[Value,Result](value,this,this)
    if(startWithMe) this.receiveFuture(event) else  this.propagateFuture(event)
    event.promise.future
  }

}

