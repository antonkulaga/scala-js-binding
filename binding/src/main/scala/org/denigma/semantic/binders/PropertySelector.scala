package org.denigma.semantic.binders
import org.denigma.binding.extensions._
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.jquery._
import org.scalax.semweb.rdf.vocabulary.XSD
import org.scalax.semweb.rdf.{IRI, RDFValue}
import org.scalax.semweb.shex.{ValueType, ValueStem}
import rx.Var

import scala.scalajs.js
import scala.scalajs.js.Date
import scala.util.Try

/**
 * Selects property from the model
 * @param el html element of the view from which there will be selection
 * @param key iri of the property
 * @param model property model with props and vals
 * @param typeHandler handler that react on typing
 */
class PropertySelector(val el:HTMLElement,val key:IRI,val model:Var[ModelInside])(typeHandler:(String)=>Unit) extends SemanticSelector
{

  def createItem(input:String):SelectOption =  {
    val value = this.parseRDF(input)
    this.makeOption(value)
  }


  protected val createHandler:js.Function1[String,SelectOption] = createItem _

  val sel: js.Dynamic = jQuery(el).dyn.selectize(selectParams(el))

  protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      delimiter = "|",
      persist = false,
      create = true,
      createItem = createHandler _,
      valueField = "id",
      labelField = "title",
      searchField = "title",
      onType = typeHandler  ,
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      options = makeOptions(),
      render =  SemanticRenderer.asInstanceOf[js.Any],
      copyClassesToDropdown = false,
      plugins = js.Array(SelectBinder.pluginName)
    )
  }
  def getValues = model.now.current.properties.getOrElse(key, Set.empty[RDFValue])//.map(v=>v.toSeq).getOrElse(Seq.empty[RDFValue]) //TODO: rewrite in favor of reactive

  def makeOptions(): js.Array[SelectOption] = js.Array(getValues.map(makeOption).toSeq:_*)

  def createFilter(input:String):Boolean = true

  def createFilterHandler :js.Function1[String,Boolean] = createFilter _



  protected def itemAddHandler(text:String, item:js.Dynamic): Unit = {
    val value = unescape(text)
    val mod = model.now
    val values = getValues
    if(values.exists(v=>v.stringValue==value)){

    }
    else
    {
      model() = mod.add(key,parseRDF(value))
    }
  }

  protected def itemRemoveHandler(text:String): Unit = {
    //dom.console.log("DELETE == "+text)
    val mod =  model.now
    val value = unescape(text)
    val remove: Set[RDFValue] = this.getValues.filter(v=>v.stringValue==value)
    for(r<-remove) {//TODO:rewrite in more effective way
      //val n = this.parseRDF(value)
      val md = mod.delete(key,r)
      model() = md
    }
  }



  /**
   * Fills values from a property model
   * @param model
   * @return
   */
  def fillValues(model: ModelInside):this.type = {
    val ss= this.selectizeFrom(el)
    val values: Set[RDFValue] = this.getValues
    val its = ss.items.toSeq.map(i=>unescape(i))
    val changed = values.exists{case v=> !its.contains(v.stringValue)}
    if(changed) {
      ss.clearOptions()
      for(v<-values){     ss.addOption(this.makeOption(v))      }
      val its = values.map(v=>escape(v.stringValue)).toSeq
      ss.addItems(js.Array(its:_*))
    }
    this
  }



}
