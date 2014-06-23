package org.denigma.binding.semantic

import org.denigma.binding.controls.AjaxModelCollection
import org.denigma.binding.extensions.sq
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.{CollectionView, OrdinaryView}
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.IRI
import rx.core.{Rx, Var}
import org.denigma.binding.extensions._
import org.scalajs.dom.extensions._

/**
 * This trait represents a view that is collection of models (Property models of RDFs)
 */
trait ModelCollection extends OrdinaryView
  with CollectionView
  with RDFView
{
  def params:Map[String,Any]

  val path:String = params.get("path").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get
  val query = IRI(params("query").toString)
  val shape = IRI(params("shape").toString)

  implicit val registry = rp


  override type Item = Var[ModelInside]
  override type ItemView =  AjaxModelCollection.ItemView

  override val items = Var(List.empty[Var[ModelInside]])

  val dirty = Rx{items().filterNot(i=>i().isUnchanged)} //TODO check how it works


  //val dirty = Rx{items().filterNot(_}

  override def newItem(item:Item):ItemView = {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]
    el.removeAttribute("data-template")
    val mp: Map[String, Any] = Map[String,Any]("model"->item)

    val view = el.attributes.get("item-view") match {
      case None=>
        AjaxModelCollection.apply(el,item)
      case Some(v)=> this.inject(v.value,el,mp) match {
        case item:ItemView=> item
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit MapView")
          AjaxModelCollection.apply(el,item)
      }
    }
    view
  }

}
