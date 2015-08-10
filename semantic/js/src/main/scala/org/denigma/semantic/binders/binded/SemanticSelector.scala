package org.denigma.semantic.binders.binded

import org.denigma.selectize.{Selector, _}
import org.denigma.semantic.extensions.GraphUpdate
import org.querki.jquery._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import rx.Rx
import rx.core.Var

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js._
import rx.extensions._


case class Typed[Rdf<:RDF](graph:PointedGraph[Rdf],typed:String)
{
  def isEmpty = typed==""
}

class SemanticSelector[Rdf<:RDF](
                                  el:HTMLElement,
                                  graph:Var[PointedGraph[Rdf]],
                                  updates:Rx[GraphUpdate[Rdf]],
                                  val predicate:Rdf#URI,
                                  createIfNOtExists:Boolean = true,separator:String="|")
                               (implicit ops:RDFOps[Rdf])

  extends Binded(graph,updates,createIfNOtExists)(ops) with Selector
{

  //val sel:Selectize
  import rx.ops._

  lazy val myGraph: Rx[PointedGraph[Rdf]] = myObjects.map { case values =>
    val triples = values.map(v => ops.makeTriple(subject, predicate, v))
    PointedGraph(subject,ops.makeGraph(triples))
  }

  def node2option(node:Rdf#Node):SelectOption =SelectOption(node2string(node),node2label(node))

  lazy val typed = Var(Typed[Rdf](myGraph.now,""))

  val options = Var(Seq.empty[Rdf#Node])

  val optionsChange = options.zip()

  val selectOptions = options.map(_.map(node2option))

  val items = myObjects.map(_.map(node2string))

  def typeHandler(typedString:String):Unit =  {typed() = Typed[Rdf](myGraph.now,typedString)}

  def makeOptions(): js.Array[SelectOption] = {
    js.Array[SelectOption]()
  }

  override protected def selectParams(el: HTMLElement): SelectizeConfigBuilder =
    SelectizeConfig
      .delimiter("|")
      .persist(false)
      .create(true)
      .createItem(this.createItem _)
      .valueField("id")
      .labelField("title")
      .searchField("title")
      .onType(typeHandler _)
      .onItemAdd(itemAddHandler _)
      .onItemRemove(itemRemoveHandler _)
      .options(js.Array(makeOptions()))
      //.render(PrefixedRenderer(prefixes))
      .copyClassesToDropdown(false)
      //.plugins(js.Array(SelectBinder.pluginName))

  def initSelectize(el:HTMLElement,params:(HTMLElement)=>SelectizeConfigBuilder): Selectize = {
    import org.denigma.binding.extensions._
    val opts:SelectizeConfig = params(el)
    val $el = $(el)
    //$el.dyn.selectize(opts)
    $el.selectize(opts)
    el.dyn.selectize.asInstanceOf[Selectize]
  }


  override protected def onObjectsChange(values: Set[Rdf#Node]): Unit = {

    //options() = orderedUpdate(options.now,values)
    //val opts = for{ n <- values}  yield node2option(n)
    //this.selectize.addItems( js.Array(opts.toSeq:_*))
  }

  val selectize = this.initSelectize(el,this.selectParams)

  def suggest(values:Seq[Rdf#Node]) =  {
    for {  r <- selectize.options.keys.filter{case k => !selectize.items.contains(k)}  } selectize.options.remove(r)
    for{ n <- values}  selectize.addOption(node2option(n))
    selectize.refreshItems()
  }

  def clearSuggestions() = {  options() = myObjects.now.toSeq  }

  def createItem(input:String):SelectOption =  SelectOption(input,node2label(string2node(input)))

  override def objectsFromHTML(): Set[Rdf#Node] = {

    dom.console.log("objects from html works!")
    Set.empty[Rdf#Node]
  }


  override protected def itemRemoveHandler(value: String): Unit = {
    dom.console.log("remove handler works!")
  }

  override protected def makeOption(vid: String, title: String): SelectOption = SelectOption(vid,title)

  override protected def itemAddHandler(value: String, item: Dynamic): Unit = {
    dom.console.log("add handler works!")
  }

  override def init() = {
    super.init()
  /*  optionsChange.onChange(ops.fromUri(predicate),true,true){case (oldVal,newVal)=>
      dom.console.log("options changed")
    }
*/
  }
}