package org.denigma.semantic.binders.shex

import org.denigma.selectize.Selectize
import org.denigma.selectors.SelectOption
import org.denigma.semantic.binders.{SelectBinder, SemanticRenderer, SemanticSelector}
import org.denigma.semantic.shapes.ArcView
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalax.semweb.rdf.RDFValue
import org.scalax.semweb.rdf.vocabulary.{RDFS, RDF}
import org.scalax.semweb.shex.ArcRule
import rx.{Rx, Var}
import org.denigma.binding.extensions._
import scala.collection.immutable.{Set, Map}
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util.{Failure, Success}
import org.scalajs.jquery._
class ValueBinder(view:ArcView,arc:Var[ArcRule], suggest:(String)=>Future[List[RDFValue]]) extends ArcBinder(view,arc)
{

  SelectBinder.activatePlugin()


  val valueSelector = new ValueClassSelector(arc,valueTypeHandler)

  def valueTypeHandler(el: HTMLElement)(str:String) = {

  }


  override protected def arcPartial(el: HTMLElement, value: String): PartialFunction[String, Unit] = {

    case "data" if value=="value" =>
      valueSelector.registerValue(el)

    case "data" if value=="value-mode" | value=="value-type" =>
      valueSelector.registerMode(el)

  }
}


import org.scalajs.dom
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex._
import rx.Var
import rx.ops._
import org.denigma.binding.extensions._
import scala.scalajs.js

/**
 * Class for ValueClass selectors
 * @param arc
 * @param typeHandler
 */
class ValueClassSelector(arc:Var[ArcRule], val typeHandler:HTMLElement=>String=>Unit) extends ModeSelector
{
  lazy val modes: Seq[String] = Seq("ValueType", "ValueSet", "ValueStem"/*, "ValueReference", "ValueAny"*/)

  lazy val mode = Var("ValueType")

  val values: Var[Set[RDFValue]] = Var(Set())

  var valueSelectors = Map.empty[HTMLElement, Selectize]
  /**
   * Handlers that fires on ark valueclass change
   * @param vc
   */
  protected def onArcValueClassChange(vc:ValueClass) = {
    vc match {
      case v: ValueType =>
        mode.set("ValueType")
        values.set(Set(v.v))


      case v: ValueAny =>
        mode.set("ValueAny")
        values.set(Set(v.stem.s))

      case v: ValueSet =>
        mode.set("ValueSet")
        values.set(v.s)

      case v: ValueStem =>
        mode.set("ValueStem")
        values.set(Set(v.s))

      case v: ValueReference =>
        mode.set("ValueReference")
        values.set(Set(v.l.asResource))
    }
    modeSelectors.values.foreach(s=>fillModeValues(mode.now,s))
    valueSelectors.values.foreach(s=>fillValues(values.now,s))
  }

  protected def fillValues(vals:Set[RDFValue],sel:Selectize) = {
    sel.clear()
    sel.clearOptions()
    for (v <- vals) {
      sel.addOption(this.makeOption(v))
    }
    val escaped = vals.map(r=>escape(r.stringValue)).toSeq
    val its:js.Array[Any] = js.Array(escaped:_*)
    sel.addItems(its)
  }


  val valueClass:Rx[ValueClass] = arc.filter(a=>a.value!=arc.now.value).map(a=>a.value)//arc.collect{case a if a.value!=arc.now.value=>arc.now.value  }
  valueClass.foreach(onArcValueClassChange)

  val currentValueClass: Rx[ValueClass] = Rx {
    mode() match {
      case "ValueType" => values() match {
        case v if v.isEmpty => ValueType(RDF.VALUE)
        case st =>
          if (st.size > 1) dom.console.error(s"WARNING valuetype with more than 1 value, only head is used")
          val res = st.collectFirst{case s:Res=>s}.getOrElse(RDF.VALUE)
          ValueType(res)
      }

      case "ValueSet" =>
        val st:Set[RDFValue] = values().map(v => v: RDFValue)
        ValueSet(st)

      case "ValueStem" =>
        values() match {
          case v if v.isEmpty => ValueStem(IRI(""))
          case st if st.head.isInstanceOf[IRI] => ValueStem(st.head.asInstanceOf[IRI])
          case st if st.size > 1 =>
            dom.console.error(s"WARNING ValueStem with more than 1 value, only head is used")
            st.collectFirst {
              case iri: IRI => ValueStem(iri)
            }.getOrElse(ValueStem(IRI("")))
        }

      case "ValueReference" =>
        dom.console.error("value reference is not implemented")
        ValueType(RDF.VALUE)

      case "ValueAny" =>
        dom.console.error("value any is not implemented")
        ValueType(RDF.VALUE)

      case other =>
        ValueType(RDF.VALUE)
    }
    ValueType(RDF.VALUE)
  }

  def onCurrentValueChange(vc:ValueClass) = {
    if(arc.now.value!=vc)  arc() = arc.now.copy(value = vc)
  }

  currentValueClass.foreach(onCurrentValueChange)

   val key = ValueType.property


  /**
   * add selectize selector to the element
   * and updates valueSelectors map with this pair
   * @param html
   */
  def registerValue(html: HTMLElement) = if (!valueSelectors.contains(html)) {
    val ss = this.initSelectize(html)
    valueSelectors = valueSelectors + (html -> ss)
    fillValues(values.now,ss)
  }

  def unregisterValue(html:HTMLElement) = valueSelectors = valueSelectors - html

  protected def valueFilter(input:String):Boolean =    {
    true
  }


  /**
   * Created an RDF item
   * @param input
   * @return
   */
  def createRDFItem(input:String):SelectOption =  {
    val value = this.parseRDF(input)
    this.makeOption(value)
  }


  protected lazy val createHandler:js.Function1[String,SelectOption] = createRDFItem _


  protected def createFilter(input:String):Boolean = true

  protected def createFilterHandler :js.Function1[String,Boolean] = createFilter _


  override protected def itemRemoveHandler(value: String): Unit = {

    val v = this.parseRDF(value)
    if(values.now.contains(v)){

      values() = values.now - v
    }
    else {
      dom.console.error(s"deleting $value that is not in the selector")
    }
  }

  override protected def itemAddHandler(value: String, item: js.Dynamic): Unit =
  {
    //nothing is needed
    val v = this.parseRDF(value)
    if(!values.now.contains(v)){
      values() = values.now + v
    } else {
      //dom.console.log("adding element that already exists")
    }
  }


  def makeOptions(): js.Array[SelectOption] = js.Array(this.values.now.map(v=>this.makeOption(v)).toSeq:_*)

  protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      delimiter = "|",
      persist = false,
      valueField = "id",
      labelField = "title",
      searchField = "title",
      onType = typeHandler _  ,
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      create = true,
      createItem = this.createHandler,
      createFilter = this.createFilterHandler,
      options = makeOptions(),
      render =  SemanticRenderer.asInstanceOf[js.Any],
      copyClassesToDropdown = false,
      plugins = js.Array(SelectBinder.pluginName)
    )
  }





}

/**
 * For compount selectors, allows to choose modes
 */
trait ModeSelector extends SemanticSelector{

  def modes:Seq[String]
  def mode:Var[String]

  def makeModeOptions():js.Array[SelectOption] = js.Array(modes.map(v => makeOption(v, v)):_*)


  var modeSelectors = Map.empty[HTMLElement, Selectize]


  protected def modeItemAddHandler(value: String, item: js.Dynamic): Unit = {
    val v = unescape(value)
    mode.set(v)
    //dom.console.log(s"adding mode element that is $value")
  }

  protected def modeItemRemoveHandler(value: String, item: js.Dynamic): Unit = {
    //mode() = unescape(value)
  }


  protected def modeSelectParams(el: HTMLElement): js.Dynamic = {
    js.Dynamic.literal(
      onItemAdd = modeItemAddHandler _,
      onItemRemove = modeItemRemoveHandler _,
      persist = false,
      maxItems = 1,
      valueField = "id",
      labelField = "id",
      searchField = "title",
      options = this.makeModeOptions(),
      render =  SemanticRenderer.asInstanceOf[js.Any],
      copyClassesToDropdown = false,
      plugins = js.Array(SelectBinder.pluginName)
    )
  }




  def registerMode(html: HTMLElement) = if (!modeSelectors.contains(html)) {
    val ss = this.initSelectize(html,this.modeSelectParams)
    //dom.console.log("one more mode")
    modeSelectors = modeSelectors + (html -> ss)
    this.fillModeValues(mode.now,ss)
  }





  protected def fillModeValues(md:String,sel:Selectize):this.type = {
    sel.clear()
    sel.clearOptions()
    for (v <- modes) {
      sel.addOption(this.makeOption(v,v))
    }
    //dom.console.log(s"${escape(md)} IDS: ${sel.options.values.map(s=>s.dyn.id).mkString(" ")}")
    //val value = escape(md)
    val its:js.Array[Any] = js.Array(md)
    sel.addItems(its)
    //dom.console.log("ITEMS = "+sel.items)
    //dom.console.log(s"add mode value: $value where options are\n ${js.JSON.stringify(sel.options)} \n AND items are:\n ${js.JSON.stringify(sel.items)}")

    this
  }


}