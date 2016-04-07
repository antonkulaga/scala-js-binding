package org.denigma.binding.binders

import org.scalajs.dom
import org.scalajs.dom.raw.{Element}
import rx._

import scala.collection.immutable.Map

object Binder{
  def apply(elemBind:(Element,Map[String,String])=>PartialFunction[(String, String), Unit]) = new ReactiveBinder {
    override def bindAttributes(el: Element, ats: Map[String, String]) = {
      val fun = elementPartial(el,ats)
      for((key,value)<-ats) fun(key,value)
      true
    }

    override def elementPartial(el: Element, ats: Map[String, String]): PartialFunction[(String, String), Unit] = elemBind(el,ats)
  }
}


trait Binder
{

  def bindAttributes(el: Element, attributes: Map[String, String] ):Boolean

}

/**
 *  A class that contains basic functions for all bindings
 */
trait ReactiveBinder extends Binder
{
  def elementPartial(el: Element, ats: Map[String, String]): PartialFunction[(String, String), Unit]

  def bindAttributes(el: Element, attributes: Map[String, String]): Boolean= {
    val fun: PartialFunction[(String, String), Unit] = elementPartial(el, attributes).orElse{case other =>}
    attributes.foreach(fun)
    true
  }


  /**
   * binds an item of Map with vars
   * @param el html element to bind to
   * @param mp map with vars
   * @param attribute name of the binding attribute (for debugging)
   * @param key kay we want to bind to
   * @param bind functions that binds
   * @tparam T type parameter for Var
   * @return
   */
  protected def bindMapItem[T](el: Element, mp: Map[String, Var[T]], attribute: String, key: String)
                          (bind: (Element, Var[T]) => Unit) =
    mp.get(key) match{
      case Some(reactive)=> bind(el, reactive)
      case None=>
        dom.console.error(s"bindMapItem: cannot bind $attribute in ${el.outerHTML} to $key")
        dom.console.log("current map =" + mp.keys.toString())
    }

  protected def dataAttributesOnly(ats: Map[String, String]): Map[String, String] = ats.collect{
    case (key, value) if key.contains("data-") && !key.contains("data-view") => (key.replace("data-", ""), value)
  }

}
