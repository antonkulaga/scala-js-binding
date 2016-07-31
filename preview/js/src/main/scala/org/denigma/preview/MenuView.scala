package org.denigma.preview

import org.scalajs.dom.raw.Element
import rx.Rx
import rx.Var

import scala.collection.immutable._


class MenuView(elem: Element) extends MapCollectionView(elem) {
    self =>

    override val items: Rx[Seq[Map[String, Any]]] = Var(
        Seq(
            Map("uri" -> "pages/bind", "label" -> "Basics"),
            /*Map("uri" -> "pages/properties", "label" -> "Property bindings"),
            */Map("uri" -> "pages/collection", "label" -> "Collection binding"),
            Map("uri" -> "pages/controls", "label" -> "Various controls"),
            Map("uri" -> "pages/charts", "label" -> "Charts"),
            Map("uri" -> "pages/start", "label" -> "Getting started"),
            Map("uri" -> "pages/pdf", "label" -> "PDF viewing"),
            Map("uri" -> "pages/rdf", "label" -> "RDF support")
        )
    )
}

import org.denigma.binding.binders.{MapItemsBinder, NavigationBinder}
import org.denigma.binding.views.{BindableView, CollectionSeqView}
import org.scalajs.dom.Element
import rx.Var

import scala.collection.immutable._

object MapCollectionView {

    class JustMapView(val elem: Element, val params: Map[String, Any]) extends BindableView
    {
        val reactiveMap: Map[String, Var[String]] = params.map(kv => (kv._1, Var(kv._2.toString)))

        this.withBinders(m => new MapItemsBinder(m, reactiveMap)::new NavigationBinder(m)::Nil)
    }

}



abstract class MapCollectionView(val elem: Element) extends CollectionSeqView
{
    override type Item = Map[String, Any]

    override type ItemView = BindableView

    def newItemView(item: Item): ItemView = this.constructItemView(item){ (el, mp) => new MapCollectionView.JustMapView(el, item) }

}