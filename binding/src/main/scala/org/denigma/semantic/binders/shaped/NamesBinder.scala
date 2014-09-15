package org.denigma.semantic.binders.shaped

import org.denigma.semantic.binders.shaped.selectors.NameTermSelector
import org.denigma.semantic.shapes.ArcView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.RDFValue
import org.scalax.semweb.shex.ArcRule
import rx.Var

import scala.collection.immutable.Map
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

class NamesBinder(view:ArcView,arc:Var[ArcRule], suggest:(String)=>Future[List[RDFValue]]) extends ArcBinder(view,arc){


  var names = Map.empty[HTMLElement,NameTermSelector]

  def nameTypeHandler(el: HTMLElement)(str:String) =
  //this.storage.read()

    this.names.get(el) match
    {
      case Some(s)=>
        debug(s"typing: $str")
        this.suggest(str).onComplete{
          case Success(options)=>s.updateOptions(options)
          case Failure(th)=>dom.console.error(s"type handler failure for with failure ${th.toString}")
        }
      case None=>dom.console.error(s"cannot find selector for property")
      //dom.console.log("typed = "+str)
    }



  override protected def arcPartial(el: HTMLElement, value: String): PartialFunction[String, Unit] = {

    case "data" if value=="name" =>
      this.bindVar("name", el: HTMLElement, this.arc) { (e,a)=>
        val sel = this.names.getOrElse(el, {
          val s = new NameTermSelector(el,a,nameTypeHandler(el) )
          names = names+ (el -> s)
          s
        })
        sel.fillValues(arc)

      }


  }
}





