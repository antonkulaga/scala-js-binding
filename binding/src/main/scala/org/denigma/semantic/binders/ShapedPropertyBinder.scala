package org.denigma.semantic.binders

import org.denigma.binding.binders.extractors.PropertyBinder
import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.jquery._
import org.scalajs.selectize.Selectize
import org.scalax.semweb.rdf.vocabulary.{RDFS, XSD}
import org.scalax.semweb.rdf.{IRI, RDFValue}
import org.scalax.semweb.shex._
import rx._
import rx.core.Var
import rx.ops._
import scalajs.concurrent.JSExecutionContext.Implicits.queue

import scala.concurrent.Future
import scala.collection.immutable._
import scala.scalajs.js
import org.denigma.binding.extensions._

import scala.scalajs.js.Date
import scala.util.{Failure, Success, Try}

class ShapedPropertySelector(val el:HTMLElement,  model:Var[ModelInside], val arc:ArcRule)
                            (typeHandler:(String)=>Unit) extends SemanticSelector
{

  val sel: js.Dynamic = jQuery(el).dyn.selectize(selectParams(el))

  def createFilter(input:String) = arc.value match {
    case ValueType(dt)=> dt match {
      case XSD.BooleanDatatypeIRI=> input.toLowerCase == "true"
      //case XSD.Date=> Date.
      case XSD.DecimalDatatypeIRI | XSD.DecimalDatatypeIRI => Try[Double](input.toDouble).isSuccess
      case XSD.IntDatatypeIRI | XSD.IntegerDatatypeIRI => Try(input.toInt).isSuccess
      case XSD.Date | XSD.DateTime => Try(new Date(input)).isSuccess
      case _=> true
    }

    //case ValueStem(st)=>
    case ValueSet(els)=> els.exists{   case v=>v.stringValue == input || v.label==input   }

    //case ValueAny(stem)=> !ex.exists{   case v=>v.stringValue == input || v.label==input   }
  }

  def createItem(input:String):SelectOption = arc.value match {
    case  ValueStem(stem: IRI) =>
      val value = if(input.startsWith(stem.stringValue)) IRI(input) else stem / input
      this.makeOption(value)
    case other=>
      this.makeOption(this.parseRDF(input))
  }

  protected val createFilterHandler:js.Function1[String,Boolean] = createFilter _
  protected val createHandler:js.Function1[String,SelectOption] = createItem _

  override protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      delimiter = "|",
      persist = true,
      valueField = "id",
      labelField = "title",
      searchField = "title",
      onType = typeHandler  ,
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      create = createHandler,
      //createFilter = createFilterHandler,
      options = makeOptions(),
      render =  SemanticRenderer.asInstanceOf[js.Any],
      copyClassesToDropdown = false,
      plugins = js.Array(SelectBinder.pluginName)
    )
  }

  protected def stemValues(stem:IRI): Iterable[RDFValue] =  {
    val str = stem.stringValue
    for {
      (key, values) <- this.model.now.current.properties
      if key.stringValue.contains(str)
      value <- values
    } yield value
  }

  protected def termValues(term:IRI): Set[RDFValue] = this.model.now.current.properties.getOrElse(term, Set.empty[RDFValue])

  /**
   * Provides arc value
   * @return
   */
  def arcValues= (arc.name match {
    case NameTerm(term)=>  termValues(term)
    case NameStem(stem)=> stemValues(stem)
    case other=> Set.empty[RDFValue]
  }).toSeq


  def makeOptions(): js.Array[SelectOption] = js.Array(this.arcValues.map(makeOption):_*)


  protected def itemAddHandler(text:String, item:js.Dynamic): Unit =  arc.name match {
      case NameTerm(term)=>
        dom.console.log(s"ITEMS ADD $text")
        val mod = model.now
        val value = unescape(text)
        mod.current.properties.get(term) match {
          case None=> model() = mod.add(term,parseRDF(value))
          case Some(ps) => if(!ps.exists(p=>p.stringValue==value)) model() = mod.add(term,parseRDF(value))
        }
      case NameStem(stem)=>
        dom.console.error("NAME STEM ITEM ADD HANDLER IS NOT YET IMPLEMENTED")
      case other =>
        dom.console.error(s"$other ITEM ADD HANDLER IS NOT YET IMPLEMENTED")

  }

  protected def itemRemoveHandler(value:String): Unit = arc.name match {
    case NameTerm(key)=>
      val mod = model.now
      val remove: Option[Set[RDFValue]] =mod.current.properties.get(key).map{ps=>  ps.collect{case p if p.stringValue==value=>p}    }
      if(remove.nonEmpty) {
        val n = this.parseRDF(value)
        val md = mod.delete(key,n)
        model() = md
       }
    case NameStem(stem)=>
      dom.console.error("NAME STEM ITEM ADD HANDLER IS NOT YET IMPLEMENTED")
    case other =>
        dom.console.error(s"$other ITEM ADD HANDLER IS NOT YET IMPLEMENTED")
  }


  /**
   * Fills values from a property model
   * @param model
   * @return
   */
  def fillValues(model: ModelInside,key:IRI):this.type = {
    val ss: Selectize = this.selectizeFrom(el)
    val its = ss.items.toSeq.map(i=>unescape(i))
    val arcs = this.arcValues

    val changed = !arcs.forall{case ar=> its.contains(ar.stringValue)}
    if(changed) {
      ss.clear()
      ss.clearOptions()
      for(v<-arcs){
        ss.addOption(this.makeOption(v))
        ss.addItem(this.escape(v.stringValue))
      }
    }
   this
  }




}


/**
 * Binder for properties
 */
class ShapedPropertyBinder(view:BindableView,modelInside:Var[ModelInside], arc:ArcRule, suggest:(IRI,String)=>Future[List[RDFValue]])
  extends ModelBinder(view,modelInside)
{

  SelectBinder.activatePlugin()
  type Selector = ShapedPropertySelector
  var selectors = Map.empty[HTMLElement,Selector]


  def typeHandler(el: HTMLElement, key: IRI)(str:String) =
  //this.storage.read()
    this.selectors.get(el) match
    {
      case Some(sel)=>
        this.suggest(key,str).onComplete{
          case Success(options)=>
            //options.foreach{ case o=> dom.console.info(s"STRING = ${o.stringValue} AND label = ${o.label}") }
            sel.updateOptions(options)
          case Failure(thr)=>dom.console.error(s"type handler failure for ${key.toString()} with failure ${thr.toString}")
        }
      case None=>dom.console.error(s"cannot find selector for ${key.stringValue}")
    }


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



  protected override def bindRdfInput(el: HTMLElement, key: IRI): Unit =
  {
   // if(arc.value.isInstanceOf[ValueSet]) dom.console.info("VALUE SET = "+arc.value.toString)

    this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (e, mod) =>

      //dom.console.log(s"CHANGE OF ${key.stringValue}")

      val sel = this.selectors.getOrElse(e, {

        val s = new ShapedPropertySelector(e, modelInside,arc)( typeHandler(e, key) )
        this.selectors = this.selectors + (e -> s)
        s
      })

      sel.fillValues(mod,key)
    }
  }

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
