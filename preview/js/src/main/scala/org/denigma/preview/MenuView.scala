package org.denigma.preview

import org.denigma.binding.views.MapCollectionView
import org.scalajs.dom.raw.Element
import rx.Rx
import rx.core.Var

import scala.collection.immutable._


class MenuView(elem: Element) extends MapCollectionView(elem) {
    self =>

    override val items: Rx[Seq[Map[String, Any]]] = Var(
        Seq(
            Map("uri" -> "pages/bind", "label" -> "Basic binding example"),
            Map("uri" -> "pages/collection", "label" -> "Collection binding"),
            Map("uri" -> "pages/controls", "label" -> "Various controls"),
            Map("uri" -> "pages/charts", "label" -> "Charts"),
            Map("uri" -> "pages/start", "label" -> "Getting started"),
            Map("uri" -> "pages/rdf", "label" -> "RDF support")
        )
    )
}