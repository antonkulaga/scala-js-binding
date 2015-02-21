package org.denigma.binding.frontend.controls

import java.util

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.extensions.sq
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.{JustPromise, PromiseEvent, BindableView}
import org.denigma.binding.views.collections.CollectionView
import org.denigma.semantic.rdf.ShapeInside
import org.denigma.semantic.shapes.{ArcView, ShapeView}
import org.denigma.semantic.storages.ShapeStorage
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.vocabulary.XSD
import org.scalax.semweb.rdf.{Res, RDFValue, vocabulary, IRI}
import org.scalax.semweb.shex.{Star, ShapeBuilder, Shape}
import rx.Rx
import rx.core.Var
import scala.collection.immutable.Map
import org.scalax.semweb.shex

import scala.concurrent.Promise
import scala.util.{Failure, Success}
import scalajs.concurrent.JSExecutionContext.Implicits.queue
object ShapeEditor{

  def apply(el:HTMLElement,mp:Map[String,Any]):ShapeView = new EditableShape(el,mp)

  lazy val testShape: Shape =  {
    val de = IRI("http://denigma.org/resource/")
    val dc = IRI(vocabulary.DCElements.namespace)
    val art = new ShapeBuilder(de / "Article_Shape")
    art has de /"is_authored_by" occurs Star //*/occurs Plus
    art has de / "is_published_in" occurs Star //occurs Plus
    art has dc / "title" occurs Star //occurs ExactlyOne
    //art has de / "date" occurs Star //occurs ExactlyOne
    art has de / "abstract" of XSD.StringDatatypeIRI  occurs Star//occurs Star
    art has  de / "excerpt" of XSD.StringDatatypeIRI  occurs Star//occurs Star
    art.result
  }
}

class ShapeEditor(val elem:HTMLElement,val params:Map[String,Any]) extends CollectionView{

  type Item = Var[ShapeInside]
  type ItemView = ShapeView


  implicit val registry = rp

  val path:String = this.resolveKey("path"){
    case v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)
  }

  val query:Option[Res] = this.resolveKeyOption("query"){
    case v=>IRI(if(v.toString.contains(":")) v.toString else sq.withHost(v.toString))
  }

  val storage = new ShapeStorage(path)

  override lazy val items:Var[List[Item]] = Var(List.empty[Item])

  override def newItem(item:Item):ItemView = this.constructItem(item,Map("shape"->item)){
    (el,mp)=>ShapeEditor(el,mp)
  }

  override def bindView(el:HTMLElement) = {
    super.bindView(el)
    storage.getShapes(this.query).onComplete{
      case Success(shapes)=>
        //dom.console.log(s"SHAPES = "+shapes.mkString)
        this.items() = shapes.map(sh=>Var(ShapeInside(sh)))
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
