package org.denigma.controls.semantic

import org.denigma.binding.extensions._
import org.denigma.binding.messages.{ExploreMessages, Filters, Sort}
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, KeyboardEvent}
import org.scalax.semweb.rdf._
import rx.core.{Rx, Var}

import scala.collection.immutable.Map
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}
/**
 * Observable colelction
 */
abstract class ExplorableCollection(name:String,elem:HTMLElement,params:Map[String,Any]) extends AjaxModelCollection(name,elem,params) {


  val filters = Var(Map.empty[IRI,Filters.Filter])
  val searchTerms = Var(Map.empty[IRI,String])
  val sorts = Var(Map.empty[IRI,Sort])

  override val explorer = Rx(ExploreMessages.Explore(
    this.query,
    this.shapeRes,
    this.filters().values.toList,
    this.searchTerms().values.toList,
    this.sorts().values.toList,
    this.exploreStorage.genId(),
    exploreStorage.channel
  )
  )

  var selectors = Map.empty[HTMLElement,FilterSelector]

  def filterTypeHandler(el: HTMLElement, key: IRI)(typed:String) =
  //this.storage.read()
    this.selectors.get(el) match
    {
      case Some(s)=>
        this.exploreStorage.suggest(typed,key,this.explorer.now).onComplete{
          case Success(sgs)=>
            //dom.console.log("options = "+sgs.options.toString())
            s.updateOptions(sgs.options)
          case Failure(th)=>dom.console.error(s"type handler failure for ${key.toString()} with failure ${th.toString}")

        }
      case None=>dom.console.error(s"cannot find selector for ${key.stringValue}")
      //dom.console.log("typed = "+str)
    }





  override def bindDataAttributes(el:HTMLElement,ats:Map[String, String]) = {
    super.bindDataAttributes(el,ats)
    this.bindExplore(el,ats)
  }


  protected def bindExplore(el:HTMLElement,ats:Map[String,String]) =for {
    (key, value) <- ats
  }{
    this.filterPartial(el,value).orElse(this.searchPartial(el,value)).orElse(this.sortPartial(el,value)).orElse(this.otherPartial)(key.toString)
  }





  protected def filterPartial(el:HTMLElement,value:String):PartialFunction[String,Unit]= {
    case "filter"=>
      this.resolve(value) match {
        case Some(key)=>
          this.bindRx(key.stringValue, el: HTMLElement, this.filters) { (e, ff) =>
            val sel = this.selectors.get(e) match {
            case Some(s)=>
              s
            //dom.console.error("second binding is not required")
            case None =>
              val s = new FilterSelector(e,key,this.filters,this.filterTypeHandler(e,key))
              this.selectors = this.selectors + (e-> s)
              s
          }
          sel.fillValues(ff)
        }
        case None=> dom.console.error(s"cannot resolve IRI for filter with $value in $id view")
      }


  }

  protected def sortPartial(el:HTMLElement,value:String):PartialFunction[String,Unit]= {
    case "sort"=>

  }

  protected def searchPartial(el:HTMLElement,value:String):PartialFunction[String,Unit]= {
    case "search"=>
      this.resolve(value) match {
        case Some(key) =>
          el.tagName match {
            case "input" | "textarea" =>
              el.onkeyup = this.onSearchKeyUp(key, el) _


          }
        case None=> dom.console.error(s"cannot resolve IRI for sort with $value in $id view")
      }
  }

  protected def onSearchKeyUp(prop:IRI,el:HTMLElement)(event:KeyboardEvent) = {
    el \ "value" match {
      case Some(att) => val value:String =  el.dyn.value.toString
        if(value=="") this.searchTerms() = this.searchTerms.now - prop else   this.searchTerms() = this.searchTerms.now + (prop->value)
      case None=> dom.console.error(s"search term in ${this.id} view is put not to input/textarea!")
    }
  }








}
