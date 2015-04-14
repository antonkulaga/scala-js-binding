package org.denigma.binding.frontend.controls

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.frontend.FrontEndStore
import org.denigma.semantic.models.WithShapeView
import org.denigma.semantic.shapes.{ArcView, ShapeView}
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.HTMLElement
import org.scalax.semweb.rdf.vocabulary.{WI, RDF}
import org.scalax.semweb.shex._
import rx._
import rx.core.Var
import rx.ops._
import org.denigma.binding.extensions._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

object EditShapeView
{

  lazy val emptyArcRule: ArcRule =  ArcRule(propertyName = RDF.VALUE)

  def apply(elem:HTMLElement,mp:Map[String,Any]) = {
    new ShapeProperty(elem,mp)
  }


}

/**
 * View for Editing of shapes
 * @param elem
 * @param params
 */
class EditShapeView (val elem:HTMLElement,val params:Map[String,Any]) extends  ShapeView with WithShapeView
{

  lazy val title = this.shapeRes.map(_.stringValue)

  val saveClick = Var(EventBinding.createMouseEvent())

  protected def onSave() = {
    val sh = shapeInside.now
    shapeInside() = sh.copy(initial = sh.current)
  }

  saveClick.handler{
    onSave()
  }

  override def newItem(item:Item):ItemView = this.constructItem(item,Map("item"->item)) { (e,m)=>
    ArcView.apply(e,m)
  }

  val onShapeChange = Obs(shapeInside,skipInitial = false){
    val cur = shapeInside.now.current
    updateShape(cur)
  }


  val addClick: Var[MouseEvent] = Var(EventBinding.createMouseEvent())

  lazy val onAddClick = rx.extensions.AnyRx(addClick).handler{
    val item: Var[ArcRule] = Var(EditShapeView.emptyArcRule)
    this.rules() = rules.now + item
  }



  protected def onSaveFileClick(): Unit= {
    val sid = this.shapeRes.now
    val quads = this.shape.now.asQuads(this.shapeRes.now)
    FrontEndStore.write(quads).onComplete{
      case Success(str)=>
        saveAs(sid.stringValue.substring(sid.stringValue.indexOf(":")+2)+".ttl",str)
      case Failure(th)=> dom.console.error("TURTLE DOWNLOAD ERROR: " +th)
    }
  }


  val saveFileClick= Var(EventBinding.createMouseEvent())

  saveFileClick.handler{
    onSaveFileClick()
  }



  override protected def attachBinders(): Unit = this.withBinders(ShapeView.defaultBinders(this))

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}



}

