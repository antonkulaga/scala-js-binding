package org.denigma.binding.frontend.controls

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.collections.CollectionView
import org.denigma.binding.views.{JustPromise, PromiseEvent}
import org.denigma.semantic.rdf.ShapeInside
import org.denigma.semantic.shapes.{ArcView, ShapeView}
import org.denigma.semantic.storages.ShapeStorage
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalax.semweb.rdf.vocabulary.{WI, XSD}
import org.scalax.semweb.rdf.{IRI, RDFValue, Res, vocabulary}
import org.scalax.semweb.shex.{Shape, ShapeBuilder, Star}
import rx.core.Var

import scala.collection.immutable.Map
import scala.concurrent.Promise
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}


object ShapeEditor{

  /**
   * Creates children Shape
   * @param el
   * @param mp
   * @return
   */
  def createItem(el:HTMLElement,mp:Map[String,Any]):ShapeView = new EditShapeView(el,mp)
}

class ShapeEditor(val elem:HTMLElement,val params:Map[String,Any]) extends CollectionView
{

  type Item = Var[ShapeInside]
  type ItemView = ShapeView



  val path:String = this.resolveKey("path"){
    case v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)
  }

  val query:Option[Res] = this.resolveKeyOption("query"){
    case v=>IRI(if(v.toString.contains(":")) v.toString else sq.withHost(v.toString))
  }

  val storage = new ShapeStorage(path)

  override lazy val items:Var[List[Item]] = Var(List.empty[Item])

  override def newItem(item:Item):ItemView = this.constructItem(item,Map("shape"->item)){
    (el,mp)=> ShapeEditor.createItem(el,mp)
  }

  override def bindView(el:HTMLElement) = {
    super.bindView(el)
    storage.getShex(this.query.getOrElse(WI.PLATFORM.HAS_SHAPE)).onComplete{
      case Success(shex)=>

        //dom.console.log(s"SHAPES = "+shapes.mkString)
        this.items() = shex.rules.map(sh=>Var(ShapeInside(sh))).toList

      case Failure(th)=>
        dom.console.error(s"shape request to ${this.path} ${this.query.map(q=>"with "+q).getOrElse("")} failed because of\n ${th.toString}")
    }
  }


  override def receiveFuture:PartialFunction[PromiseEvent[_,_],Unit] = {

    case JustPromise(ArcView.SuggestNameTerm(typed),origin,latest,bubble,promise:Promise[collection.Seq[RDFValue]])=>
      storage.suggestProperty(typed).onComplete
      {
        case Success(res)=>
          promise.success(res.options)
        case Failure(th)=>
          promise.failure(th)
          dom.console.error("suggession is broken"+th)
      }

    case ev=>
      //debug("propogation")
      this.propagateFuture(ev)

  }

  protected override def attachBinders(): Unit =  this.withBinders(new GeneralBinder(this))

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
}
