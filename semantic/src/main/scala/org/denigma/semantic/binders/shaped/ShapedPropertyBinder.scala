package org.denigma.semantic.binders.shaped

import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.{BinderWithSelection, ModelBinder, SelectBinder}
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalax.semweb.rdf.{IRI, RDFValue}
import org.scalax.semweb.shex._
import rx._
import rx.core.Var
import rx.ops._
import scala.collection
import scala.collection.immutable._
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

/**
 * Binder for properties
 */
class ShapedPropertyBinder(view:BindableView,modelInside:Var[ModelInside], arc:ArcRule)(val suggest:(IRI,String)=>Future[collection.Seq[RDFValue]])
  extends ModelBinder(view,modelInside) with BinderWithSelection[ShapedPropertySelector]
{

  SelectBinder.activatePlugin()

  protected def withTerm(mod:PropertyModel,term:IRI): PropertyModel = if(mod.properties.contains(term)) mod else
    mod.copy(properties = mod.properties.updated(term,Set.empty))


  def defineArc(rule:ArcRule) = {

    rule.name match { //RDFa mark
      case NameTerm(term)=>
        val mod = modelInside.now
        if(!mod.current.properties.contains(term)) modelInside() = mod.copy(initial = withTerm(mod.initial,term), current = withTerm(mod.current,term))
      case _=>
    }
  }

  this.defineArc(arc)


  /**
   * Main partial function for parsing RDF data
   * @param el html element to bind to
   * @param key Key
   * @param value Value
   * @param ats attributes
   * @return
   */
  protected override def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] =
    this.vocabPartial(value).orElse(this.arcPartial(el, value)).orElse(this.propertyPartial(el, key, value, ats))


  lazy val arcProps: Rx[Map[IRI, Set[RDFValue]]] = modelInside.map { case m => m.current.properties.collect {
          case (key,values) if arc.name.matches(key)=> (key,values)   }
    }

  protected def stemValues(stem:IRI): Iterable[RDFValue] =  {
    val str = stem.stringValue
    for {
      (key, values) <- this.modelInside.now.current.properties
      if key.stringValue.contains(str)
      value <- values
    } yield value
  }

  protected def termValues(term:IRI): Set[RDFValue] = this.modelInside.now.current.properties.getOrElse(term, Set.empty[RDFValue])

  /**
   * Provides arc value
   * @return
   */
  def arcValues= (arc.name match {
    case NameTerm(term)=>  termValues(term)
    case NameStem(stem)=> stemValues(stem)
    case other=> Set.empty[RDFValue]
  }).toSeq


  protected override def bindRdfInput(el: HTMLElement, key: IRI): Unit =
  {

    this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (e, mod) =>

      val sel = this.selectors.getOrElse(e, {
        val s = new ShapedPropertySelector(e, key,modelInside,arc)( typeHandler(e, key) )
        this.selectors = this.selectors + (e -> s)
        s
      })
      sel.sel.lock()

      sel.fillValues(mod)
    }
  }

  def arcPartial(el:HTMLElement,value:String):PartialFunction[String,Unit] = {

    case "data" if value=="value"=>
      this.arcProps.now.keys.headOption match {
        case Some(k)=>
          this.bindRDFProperty(el,k) //TODO: rewrite
        case None=>  //dom.console.error(s"no property found for the shape in arc ${arc.name.toString}")
      }

    case "data" if value=="name"=> arc.title match {
      case Some(tlt)=> this.setTitle(el,tlt)
      case None=> setTitle(el,arcProps.now.keys.head.label)
    }
  }

}
