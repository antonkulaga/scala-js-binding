package org.denigma.binding.views

import scala.concurrent.Future

/**
 * View that can have bubbling events
 */
/*

trait BindingEvent
{
  type Origin
  val origin:Origin//BasicView
  val latest:BasicView
  val bubble:Boolean

  def withCurrent(cur:BasicView):this.type
}

case class JustPromise[Value,Result](value:Value,origin:BasicView,latest:BasicView, bubble:Boolean = true, promise:Promise[Result] = Promise[Result]()) extends PromiseEvent[Value,Result] {
  type Origin = BasicView

  override def withCurrent(cur:BasicView): this.type= this.copy(latest = cur).asInstanceOf[this.type]
}

trait PromiseEvent[Value,Result] extends BindingEvent
{
  val value:Value
  val promise:Promise[Result]
}


trait BubbleView {
  self:OrganizedView=>
  /**
   * Event subsystem
   * @return
   */
  def receive:PartialFunction[BindingEvent,Unit] = {
    case event:BindingEvent=> this.propagate(event)
  }

  def receiveFuture:PartialFunction[PromiseEvent[_,_],Unit] = {
    case ev=>this.propagateFuture(ev)
  }


  /**
   * Propogates future to the top for some reason
   * @param event
   * @return
   */
  protected def propagateFuture(event:PromiseEvent[_,_]) =
    this.nearestParentOf{  case p:ReactiveView=>p  } match {
      case Some(p)=> p.receiveFuture(event.withCurrent(this))
      case None=> event.promise.failure(new Exception(s"could not find value for ${event.origin}"))
    }




  /**
   * Fires an event
   * @param event
   * @param startWithMe
   */
  def fire(event:BindingEvent,startWithMe:Boolean = false): Unit = if(startWithMe) this.receive(event) else  this.propagate(event)

  protected def propagate(event:BindingEvent) =   this.nearestParentOf{  case p:ReactiveView=>p  } match {
    case Some(p)=> p.receive(event.withCurrent(this))
    case None=>
  }


  /**
   * Asks parent to provide some future
   * @param value
   * @param startWithMe
   * @tparam Value
   * @tparam Result
   * @return
   */
  def ask[Value,Result](value:Value,startWithMe:Boolean = false):Future[Result] = {
    val event = new JustPromise[Value,Result](value,this,this)
    if(startWithMe) this.receiveFuture(event) else  this.propagateFuture(event)
    event.promise.future
  }

}
*/
