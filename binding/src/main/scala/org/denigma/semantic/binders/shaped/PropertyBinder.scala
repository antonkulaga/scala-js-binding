package org.denigma.semantic.binders.shaped

import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.SelectBinder
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.{RDFValue, IRI}
import org.scalax.semweb.shex.ArcRule
import rx.Rx
import rx.core.Var
import rx.ops._

import scala.collection.immutable.Map
import scala.concurrent.Future

/**
 * Binder for properties
 */
class PropertyBinder(view:BindableView,modelInside:Var[ModelInside], arc:ArcRule, suggest:(IRI,String)=>Future[List[RDFValue]])
  extends SelectBinder(view,modelInside,suggest){

  /**
   * Returns partial function that binds to RDF
   * @param el html element to bind to
   * @param key Key
   * @param value Value
   * @param ats attributes
   * @return
   */
  protected override def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] =
    this.vocabPartial(value).orElse( this.propertyPartial(el,key,value,ats))

  val arcProps: Rx[Map[IRI, Set[RDFValue]]] = model.map{  case m => m.current.properties.collect{ case (key,values) if arc.name.matches(key)=>
      (key,values)
      }
    }


  /**TODO  rewrite*/
  def arcPartial(el:HTMLElement,value:String):PartialFunction[String,Unit] = {

    case "data" if value=="value"=>
      this.arcProps.now.keys.headOption match {
        case Some(key)=>
          this.bindRDFProperty(el,key) //TODO: rewrite
        case None=> error(s"no property found for the shape")
      }

    case "data" if value=="name"=> arc.title match {
      case Some(t)=>

        this.bindRx("data-name",el,this.model){ (e,m)=>
          //write something
        }

      case None=> arcProps.now.keys.head
    }
  }

}
