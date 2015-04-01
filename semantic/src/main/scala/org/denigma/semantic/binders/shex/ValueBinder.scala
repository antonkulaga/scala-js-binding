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
  lazy val modes: Seq[String] = Seq("ValueType", "ValueSet", "ValueStem", "ValueReference", "ValueAny")

  lazy val mode = Var("ValueType")

  val values: Var[Set[RDFValue]] = Var(Set())

  var valueSelectors = Map.empty[HTMLElement, Selectize]
  /**
   * Handlers that fires on ark valueclass change
   * @param vc
   */
  def onArcValueChange(vc:ValueClass) = vc match {
    case v: ValueType =>
      values.updateSilent(Set(v.v))
      mode() = "ValueType"
    case v: ValueAny =>
      values.updateSilent(Set(v.stem.s))
      mode() = "ValueAny"
    case v: ValueSet =>
      values.updateSilent(v.s)
      mode() =  "ValueSet"
    case v: ValueStem =>
      values.updateSilent(Set(v.s))
      mode() = "ValueStem"
    case v: ValueReference =>
      values.updateSilent(Set(v.l.asResource))
      mode() = "ValueReference"
  }

  arc.foreach{a=>
    onArcValueChange(a.value)
  }

  values.foreach{v=>
    dom.console.log("CHANGING VALUE SELECTORS")
    this.valueSelectors.values.foreach { case sel =>   fillValues(sel,v) }
  }

  def fillValues(sel:Selectize,v:Set[RDFValue] = values.now) = {
    sel.clear()
    sel.clearOptions()
    for (v <- modes) {
      sel.addOption(this.makeOption(v))
    }
    val escaped = v.map(r=>escape(r.stringValue)).toSeq
    val its:js.Array[Any] = js.Array(escaped:_*)
    sel.addItems(its)
    this
  }

  val valueClass: Rx[ValueClass] = Rx {
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

  valueClass.filter(vc=>this.arc.now.value!=vc).foreach{vc=>
    arc() = arc.now.copy(value = vc)
  }

   val key = ValueType.property


  def registerValue(html: HTMLElement) = if (!valueSelectors.contains(html)) {
    val ss = this.initSelectize(html)
    valueSelectors = valueSelectors + (html -> ss)
    fillValues(ss)
  }

  def unregisterValue(html:HTMLElement) = valueSelectors = valueSelectors - html

  protected def valueFilter(input:String):Boolean =    {
    true
  }


  def createRDFItem(input:String):SelectOption =  {
    val value = this.parseRDF(input)
    this.makeOption(value)
  }


  protected val createHandler:js.Function1[String,SelectOption] = createRDFItem _


  protected def createFilter(input:String):Boolean = true

  protected def createFilterHandler :js.Function1[String,Boolean] = createFilter _


  override protected def itemRemoveHandler(value: String): Unit = {
    //nothing is needed
    val v = this.parseRDF(value)
    if(values.now.contains(v)){
      values() = values.now + v
    } else {
      dom.console.log("adding element that already exists")
    }
  }

  override protected def itemAddHandler(value: String, item: js.Dynamic): Unit = {
    val v = this.parseRDF(value)
    if(values.now.contains(v)){

      values() = values.now - v
    }
    else {
      dom.console.error(s"deleting $value that is not in the selector")
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
      createItem = this.createHandler _,
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

  mode.foreach{case m=>
    this.modeSelectors.values.foreach { case sel =>  this.fillModeValues(sel,m) }
  }



  protected def modeItemAddHandler(value: String, item: js.Dynamic): Unit = {
    val v = unescape(value)
    dom.console.log(v)
    if(mode.now!=v)  mode() = v
  }

  protected def modeItemRemoveHandler(value: String, item: js.Dynamic): Unit = {
    //mode() = unescape(value)
  }


  protected def modeSelectParams(el: HTMLElement): js.Dynamic = {
    js.Dynamic.literal(
      onItemAdd = modeItemAddHandler _,
      onItemRemove = modeItemRemoveHandler _,
      maxItems = 1,
      value = escape(mode.now),
      valueField = "id",
      labelField = "title",
      searchField = "title",
      options = this.makeModeOptions()
    )
  }




  def registerMode(html: HTMLElement) = if (!modeSelectors.contains(html)) {
    val ss = this.initSelectize(html,this.modeSelectParams)
    modeSelectors = modeSelectors + (html -> ss)
    this.fillModeValues(ss)
  }




  /**
   * Fill values of the selector
   * @param md
   * @return
   */

  def fillModeValues(sel:Selectize,md:String = mode.now):this.type = {
    sel.clear()
    sel.clearOptions()
    for (v <- modes) {
      val opt = this.makeOption(v)
      //dom.console.log(opt.toString)
      sel.addOption(opt)
    }
    val its: js.Array[Any] = js.Array(escape(md))
    sel.addItems(its)
    this
  }





}