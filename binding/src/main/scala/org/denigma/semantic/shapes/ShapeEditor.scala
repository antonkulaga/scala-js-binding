package org.denigma.semantic.shapes

import org.denigma.binding.extensions.sq
import org.denigma.binding.views.{CollectionView, BindableView}
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.Shape
import rx.core.{Var, Rx}

//abstract class ShapesEditor(val elem:HTMLElement,val params:Map[String,Any]) extends OrdinaryView with CollectionView
// {
//
//   val path:String = params.get("path").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get
//
//
//   override type Item = Shape
//
//   override def newItem(mp: Item): ItemView = {
//     ???
//   }
//
//   override type ItemView = ShapeView
//
//   override val items: Rx[List[Item]] = Var(List.empty)
//
// }
