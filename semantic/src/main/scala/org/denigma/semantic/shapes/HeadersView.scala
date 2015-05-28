package org.denigma.semantic.shapes

import org.denigma.binding.binders.{NavigationBinding, GeneralBinder}
import org.denigma.binding.views.OrganizedView
import org.denigma.semantic.binders.shex.ArcBinder
import org.denigma.semantic.models.WithShapeView
import org.denigma.semantic.rdf.ShapeInside
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.denigma.semweb.shex.{ArcRule, Shape}
import rx._
import org.denigma.binding.views.collections.CollectionView
import scala.collection.immutable.Map
import rx.extensions._
import rx.ops._

object HeadersView{
  def apply(el:HTMLElement,params:Map[String,Any]) = new JustHeaderView(el,params)


  class JustHeaderView(val elem:HTMLElement, val params:Map[String,Any]) extends ArcView {

    override def activateMacro(): Unit = {extractors.foreach(_.extractEverything(this))}

    override protected def attachBinders(): Unit =binders =  new ArcBinder(this,this.arc)::new GeneralBinder(this)::Nil

  }


}


class HeadersView(val elem:HTMLElement, val params:Map[String,Any],father:Option[OrganizedView]) extends CollectionView with WithShapeView
{
  type Item = Var[ArcRule]

  type ItemView = ArcView

  override lazy val shapeInside:Var[ShapeInside] = this.shapeOption.orElse {
    father.collectFirst{
      case s:WithShapeView=>s.shapeInside
    }
  }.getOrElse(   Var(ShapeInside.apply(Shape.empty))  )

  val items = shapeInside.map(sh=>sh.current.arcSorted.map(r=>Var(r)))

  override def newItem(item:Item):ItemView = this.constructItem(item,Map("item"->item)) { (e,m)=> HeadersView.apply(e,m) }

  override protected def attachBinders(): Unit =binders =  new GeneralBinder(this)::new NavigationBinding(this)::Nil

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

}