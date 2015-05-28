package org.denigma.binding.views

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement

import scala.collection.immutable.Map
import scala.concurrent.{Future, Promise}
import org.scalajs.dom.ext._
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


/**
 * View that supports resolving some data from params as well as pattern matching on parents and events
 */
abstract class ReactiveView extends OrganizedView{

  def params:Map[String,Any]
/*
  protected var cache:Map[String,Any] = this.params //for resolutions

  protected def cached[T](key:String,value:T) = { //TODO: delete from here
    cache = cache + (key->value)
    value
  }*/

  protected def resolveMyKey[Result](key:String)(fun:PartialFunction[Any,Result]) = params.get(key).collectFirst(fun)

  def resolveKeyOption[Result](key:String, who:ReactiveView = this)(fun:PartialFunction[Any,Result]):Option[Result] = {
    who.resolveMyKey(key)(fun) match {
      case None=>
        who.nearestParentOf{case p:ReactiveView=>p.resolveKeyOption(key)(fun)}.flatten
      case other=> other
    }
  }

  override protected def attributesToParams(el:HTMLElement): Map[String, Any] = el.attributes
    .collect{
    case (key,value) if key.contains("data-param-")=>
      val k = key.replace("data-param-", "")
      val v = value.value
      if(v.startsWith("parent."))
      {
        val pn = v.replace("parent.","")
        this.params.get(pn) match {
          case Some(p)=>
            k->p.asInstanceOf[Any]
          case None=>
            dom.console.log(s"could not find data-param $pn for child")
            k -> pn
        }
      } else k -> v.asInstanceOf[Any]


  }.toMap

  /**
   * Resolves mandatory keys from either this view or parent view
   * @param key
   * @param fun
   * @tparam Result
   * @return
   */
  def resolveKey[Result](key:String)(fun:PartialFunction[Any,Result]):Result = this.resolveKeyOption(key)(fun) match {
    case Some(res) =>res
    case None=>
      dom.console.error(s"cannot find appropriate value for mandatory param $key in view $id with the following HTML:\n${elem.outerHTML}\n")
      ???
  }



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
