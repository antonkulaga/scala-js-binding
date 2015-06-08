package org.denigma.semantic.binders

import org.denigma.selectize.{SelectizeConfigBuilder, SelectizeConfig, SelectOption}
import org.denigma.semantic.rdf.ModelInside
import org.denigma.semweb.rdf.{IRI, RDFValue}
import org.scalajs.dom.raw.HTMLElement
import rx.Var

import scala.scalajs.js

/**
 * Selects property from the model
 * @param el html element of the view from which there will be selection
 * @param key iri of the property
 * @param model property model with props and vals
 * @param typeHandler handler that react on typing
 */
class PropertySelector(
                        val el:HTMLElement,
                        val key:IRI,
                        val model:Var[ModelInside],
                        val prefixes:Var[Map[String,IRI]] = Var(RDFBinder.defaultPrefixes))
                      (typeHandler:(String)=>Unit)
  extends SemanticSelector
{

  lazy val sel= this.initSelectize(el)// jQuery(el).dyn.selectize(selectParams(el)).asInstanceOf[Selectize]


  def createItem(input:String):SelectOption =  {
    val value = this.parseRDF(input)
    this.makeOption(value)
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
      .onType(typeHandler)
      .onItemAdd(itemAddHandler _)
      .onItemRemove(itemRemoveHandler _)
      .options(js.Array(makeOptions()))
      .render(PrefixedRenderer(prefixes))
      .copyClassesToDropdown(false)
      .plugins(js.Array(SelectBinder.pluginName))



  /*  js.Dynamic.literal(
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
      render = PrefixedRenderer(prefixes).asInstanceOf[js.Any],
      copyClassesToDropdown = false,
      plugins = js.Array(SelectBinder.pluginName)
    )
  */

  def getValues = model.now.current.properties.getOrElse(key, Set.empty[RDFValue])//.map(v=>v.toSeq).getOrElse(Seq.empty[RDFValue]) //TODO: rewrite in favor of reactive

  def makeOptions(): js.Array[SelectOption] = js.Array(getValues.map(makeOption).toSeq:_*)

  def createFilter(input:String):Boolean = true

  def createFilterHandler :js.Function1[String,Boolean] = createFilter _



  protected def itemAddHandler(text:String, item:js.Dynamic): Unit = {
    //val value = unescape(text)
    val value = text
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
    //val value = unescape(text)
    val value = text
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
     val values: Set[RDFValue] = this.getValues
    //val sel =    this.selectizeFrom(el)
    val its = sel.items//.toSeq.map(i=>unescape(i))
    val changed = values.exists{case v=> !its.contains(v.stringValue)}
    if(changed) {
      sel.clearOptions()
      for(v<-values){     sel.addOption(this.makeOption(v))      }
      val its = values.map(v=>/*escape*/(v.stringValue)).toSeq
      sel.addItems(js.Array(its:_*))
    }
    this
  }



}


