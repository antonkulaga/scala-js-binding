package org.denigma.semantic.binders.binded
/*
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.denigma.binding.extensions._
import org.denigma.selectize.{Selector, _}
import org.denigma.semantic.extensions.GraphUpdate
import org.querki.jquery._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import org.w3.banana.binder.NodeBinder
import org.w3.banana.syntax.RDFSyntax
import rx.Rx
import rx.core.Var

import scala.scalajs.js
import scala.scalajs.js._
import scala.util.Try




object SelectizeBinders{

  implicit def apply[Rdf<:RDF](implicit ops:RDFOps[Rdf]) = new SelectizeBinders[Rdf](ops)
}

/**
 * I know it is ugly but I am not good at default banana-rdf binders
 * @param ops
 * @tparam Rdf
 */
class SelectizeBinders[Rdf<:RDF](ops:RDFOps[Rdf]) {
  import ops._

  def node2label(node:Rdf#Node) = ops.foldNode(node)(
    ops.lastSegment,
    ops.fromBNode,
    l => ops.fromLiteral(l)._1
  )

  def node2text(node:Rdf#Node) = ops.foldNode(node)(
    ops.fromUri,
    ops.fromBNode,
    l => ops.fromLiteral(l) match {
      case (str, t, Some(lang)) => "\""+str+"\""+"@"+lang
      case (str, t, _) =>  "\""+str+"\""+"^^"+ops.fromUri(ops.xsd.string)
      //case (str,_,_)=>str
    }
  )

  def node2selectOption(node:Rdf#Node) =  SelectOption(node2text(node),node2label(node))

  def text2node(string:String) = string match {
    case lit if lit.contains("\"^^") =>
      val ind = lit.indexOf("\"^^")
      val (label, dt) = (lit.substring(1, ind), lit.substring(ind + 3, lit.length))
      //dom.console.log("DAT IS\n"+dt)
      //dom.console.log("URI is\n"+ops.makeUri(dt))
      ops.makeLiteral(label, ops.makeUri(dt))

    case lit if lit.contains("\"@") =>
      val ind = lit.indexOf("\"@")
      ops.makeLiteral(lit.substring(0, ind), ops.makeUri(lit.substring(ind + 2, lit.length)))

    case uri if uri.contains(":") => ops.makeUri(uri.replace(' ', '_'))

    case other => ops.makeLiteral(other, ops.xsd.string)
  }


  implicit def textNodeBinder = new NodeBinder[Rdf,String] {
    override def toNode(t: String): Rdf#Node = text2node(t)

    override def fromNode(node: Rdf#Node): Try[String] = Try(node2text(node))
  }


  implicit def selectOptionNodeBinder = new NodeBinder[Rdf,SelectOption] {
    override def toNode(t: SelectOption): Rdf#Node = t.id.toNode

    override def fromNode(node: Rdf#Node): Try[SelectOption] = Try(node2selectOption(node))
  }

}


trait LightSelector //Selector in Selectize extensions is too heavy for my purpose
{
  protected def itemAddHandler(value:String, item:js.Dynamic): Unit

  protected def itemRemoveHandler(value:String): Unit

  protected def selectParams(el: HTMLElement):SelectizeConfigBuilder

  protected def selectizeFrom(el:HTMLElement): Selectize = {
    val s = el.asInstanceOf[js.Dynamic].selectize
    s.asInstanceOf[Selectize]
  }

  def selectizeOption(el:HTMLElement):Option[Selectize] =   el.asInstanceOf[js.Dynamic].dyn.selectize match {
    case s if s==null | js.isUndefined(s)=>None
    case s=>Some(s.asInstanceOf[Selectize])
  }
}

class SemanticSelector[Rdf<:RDF](
                                  el:HTMLElement,
                                  graph:Var[PointedGraph[Rdf]],
                                  updates:Rx[GraphUpdate[Rdf]],
                                  val predicate:Rdf#URI,
                                  createIfNotExists:Boolean = true,
                                  separator:String="|"
                                  )
                               (implicit ops:RDFOps[Rdf], binders:SelectizeBinders[Rdf])
  extends Binded(graph,updates,createIfNotExists)(ops) with LightSelector
{
  import binders._


  //val sel:Selectize
  import ops._
  import rx.ops._

  lazy val myGraph: Rx[PointedGraph[Rdf]] = myObjects.map { case values =>
    val triples = values.map(v => ops.makeTriple(subject, predicate, v))
    PointedGraph(subject,ops.makeGraph(triples))
  }

  val typed = Var(Typed[Rdf](myGraph.now,""))

  val suggestions: Var[Set[Rdf#Node]] = Var(Set.empty[Rdf#Node])

  val itemOptions: Rx[Set[SelectOption]] = myObjects.map(_.map(node2selectOption))

  val items: Rx[Set[String]] = itemOptions.map(_.map(_.id))

  val allOptions = Rx{ itemOptions()->suggestions().map(node2selectOption) }

  val optionChanges = allOptions.zip

  def typeHandler(typedString:String):Unit =  {
    //dom.console.log(s"typed: ${typedString}")
    typed() = Typed[Rdf](myGraph.now,typedString)
  }

  override protected def selectParams(el: HTMLElement): SelectizeConfigBuilder =
    SelectizeConfig
      .delimiter(separator)
      .persist(false)
      .create(true)
      .createItem(this.createItem _)
      .valueField("id")

      .labelField("title")
      .searchField("title")
      .onType(typeHandler _)
      .onItemAdd(itemAddHandler _)
      .onItemRemove(itemRemoveHandler _)
      //.render(PrefixedRenderer(prefixes))
      .copyClassesToDropdown(false)
      .plugins(js.Array(BetterDropdownPlugin.pluginName))

  def initSelectize(el:HTMLElement,params:(HTMLElement)=>SelectizeConfigBuilder): Selectize = {
    import org.denigma.binding.extensions._
    val opts:SelectizeConfig = params(el)
    val $el = $(el)
    //$el.dyn.selectize(opts)
    $el.selectize(opts)
    el.dyn.selectize.asInstanceOf[Selectize]
  }


  override protected def onObjectsChange(values: Set[Rdf#Node]): Unit = {
    dom.console.log(s"onObjectChange with values ${values}")
  }

  lazy val selectize = this.initSelectize(el,this.selectParams)

  def createItem(input:String):SelectOption =  {
    dom.console.log(s"CREATE ITEM IS CALLED for $input")
    SelectOption(input,node2label(input.toNode))
  }

  override def objectsFromHTML(): Set[Rdf#Node] = {

    //dom.console.log("objects from html works!")
    Set.empty[Rdf#Node]
  }


  override protected def itemRemoveHandler(value: String): Unit = {
    dom.console.log(s"remove handler works for $value")
    myObjects.set(myObjects.now - value.toNode)
  }

  override protected def itemAddHandler(value: String, item: Dynamic): Unit = {
    val node = value.toNode
    dom.console.log(s"add handler works for $value with ${node}")
    suggestions() = Set.empty
    myObjects.set(myObjects.now + node)
  }

  protected def updateOptions(values:Set[SelectOption]) = {
    val dic = js.Dictionary[Object](values.toSeq.map(v=>v.id->v.asInstanceOf[Object]):_*)
    selectize.options = dic
    selectize.refreshOptions(false)
  }

  protected def updateItems() = {
    val updated =  selectize.items.toSeq.updatedBy(items.now)
    selectize.items = js.Array[String](updated:_*)
    selectize.addItems(js.Array(updated:_*))
    selectize.refreshItems()
    info("onItemsChanged")
  }

  protected def onOptionsChange(change:(
    (Set[SelectOption], Set[SelectOption]),
    (Set[SelectOption], Set[SelectOption])
    ) ) = change match {
      case ((oldItems,oldSug),(newItems,newSug))=>
        updateOptions(newItems++newSug)
        if(oldItems!=newItems || selectize.items.length!=newItems.size) updateItems()
        info("OPTIONS CHANGE")
  }

  protected def info(action:String) = {
    dom.console.log(
      s"""
         |-------  $action  -----------------------------------
         |MY_OBJECTSS: ${myObjects.now.foldLeft("") { case (acc, e) => acc + " | " + e.toString }}
         |MY_OPTIONS: ${(allOptions.now._1++allOptions.now._2).foldLeft("") { case (acc, e) => acc + " | " + e.toString }}
         |MY_ITEMS: ${itemOptions.now.map(_.id)}
         |OPTIONS: ${selectize.options.values.foldLeft(""){ case (acc,e)=>acc+" | "+e.toString}  }
         |ITEMS: ${selectize.items}
         |******************************************************
      """.stripMargin
    )
  }

  override def subscribe() = {
    //SelectPlugin.activatePlugin()
    BetterDropdownPlugin.activatePlugin()
    import rx.ops._
    if(!propertyExists){
      if(createIfNotExists) {
        //if no properties exist then creates them
        updateFromHTML()
      } else dom.console.error(s"property $predicate does not exist in RDF graph")
    }
    //subscribes and runs myObjects change
    //myObjects.foreach(onObjectsChange)

    //subscribes and runs updates
    updates.foreach(onUpdate)

    optionChanges.onChange(ops.fromUri(predicate),uniqueValue = true,skipInitial = false)(onOptionsChange)
  }

  subscribe()
}
*/