package org.denigma.semantic.binders.shaped

import org.denigma.semantic.binders.selectors.{NameTermSelector, ArcSelector}
import org.denigma.semantic.shapes.ArcView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.{IRI, RDFValue}
import org.scalax.semweb.shex.{ArcRule, _}
import rx.Var
import rx.ops._

import scala.collection.immutable.Map
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Success, Failure}
import scalajs.concurrent.JSExecutionContext.Implicits.queue

class NamesBinder(view:ArcView,arc:Var[ArcRule], suggest:(IRI,String)=>Future[List[RDFValue]]) extends ArcBinder(view,arc){


  var names = Map.empty[HTMLElement,NameTermSelector]

  def nameTypeHandler(el: HTMLElement, key: IRI)(str:String) =
  //this.storage.read()
    this.names.get(el) match
    {
      case Some(s)=>
        this.suggest(key,str).onComplete{
          case Success(options)=>s.updateOptions(options)
          case Failure(th)=>dom.console.error(s"type handler failure for ${key.toString()} with failure ${th.toString}")
        }
      case None=>dom.console.error(s"cannot find selector for ${key.stringValue}")
      //dom.console.log("typed = "+str)
    }


  override protected def arcPartial(el: HTMLElement, value: String): PartialFunction[String, Unit] = {


    case "data" if value=="name" => // debug("name")
      this.bindVar("name", el: HTMLElement, this.arc) { (e,arc)=>
        val sel = this.names.getOrElse(el, {
          val s = new NameTermSelector(el,arc,value=>())
          names = names+ (el -> s)
          s
        })
        sel.fillValues(arc)

      }


  }
}





