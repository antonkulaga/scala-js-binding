package org.denigma.semantic.binders

import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.selectize.Selectize
import org.scalax.semweb.rdf.{IRI, RDFValue}
import org.scalax.semweb.shex.ArcRule
import rx._
import rx.core.Var
import rx.ops._

import scala.collection.immutable.Map
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Any


class ShapedPropertySelector(el:HTMLElement, key:IRI, model:Var[ModelInside], arc:ArcRule)(typeHandler:(String)=>Unit)
  extends PropertySelector(el,key,model)(typeHandler)
{



}

/**
 * Binder for properties
 */
class ShapedPropertyBinder(view:BindableView,modelInside:Var[ModelInside], arc:ArcRule, suggest:(IRI,String)=>Future[List[RDFValue]])
  extends SelectBinder(view,modelInside,suggest)
{


  protected override def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] =
  {
    this.vocabPartial(value).orElse(this.arcPartial(el, value)).orElse(this.propertyPartial(el, key, value, ats))
  }

  val arcProps: Rx[Map[IRI, Set[RDFValue]]] = model.map{  case m => m.current.properties.collect{ case (key,values) if arc.name.matches(key)=>
      (key,values)
      }
    }


  protected override def bindRdfInput(el: HTMLElement, key: IRI): Unit =
  {
    this.bindRx(key.stringValue, el: HTMLElement, model) { (e, mod) =>
      val sel = this.selectors.getOrElse(e, {
        val s = new ShapedPropertySelector(e, key, model,arc)( typeHandler(e, key) )
        this.selectors = this.selectors + (e -> s)
        s
      })

      sel.fillValues(mod)
    }
  }


  /**TODO  rewrite*/
  def arcPartial(el:HTMLElement,value:String):PartialFunction[String,Unit] = {

    case "data" if value=="value"=>
      this.arcProps.now.keys.headOption match {
        case Some(key)=>
          this.bindRDFProperty(el,key) //TODO: rewrite
        case None=>  //dom.console.error(s"no property found for the shape in arc ${arc.name.toString}")
      }

    case "data" if value=="name"=> arc.title match {
      case Some(tlt)=> this.setTitle(el,tlt)
      case None=> setTitle(el,arcProps.now.keys.head.label)
    }
  }

}
