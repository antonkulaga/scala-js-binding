package org.denigma.semantic.grids

import org.denigma.binding.messages.Filters
import org.denigma.binding.messages.Filters.ValueFilter
import org.denigma.selectize.{SelectizeConfigBuilder, SelectOption, SelectizeConfig}
import org.denigma.semantic.binders.{SelectBinder, PrefixedRenderer, RDFBinder, SemanticSelector}
import org.denigma.semweb.rdf.IRI
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx.Var

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.JSON

abstract class ModifierSelector[T](val el:HTMLElement, val key:IRI, val modifiers:Var[Map[IRI,T]], typeHandler:(String)=>Unit, val prefixes:Var[Map[String,IRI]] = Var(RDFBinder.defaultPrefixes))
  extends SemanticSelector{

  override def itemRemoveHandler(value:String): Unit = {
    this.modifiers() = this.modifiers.now - key
  }

}


/**
  * Selector that provides suggessions for filter fields
  * @param el
  * @param key
  * @param modifiers
  * @param typeHandler
  */
class FilterSelector(el:HTMLElement, key:IRI, modifiers:Var[Map[IRI,Filters.Filter]], typeHandler:(String)=>Unit, prefs:Var[Map[String,IRI]] = Var(RDFBinder.defaultPrefixes))
  extends ModifierSelector[Filters.Filter](el,key,modifiers,typeHandler,prefs){

  lazy val sel = this.initSelectize(el)

   protected def propertyFilterOption = this.modifiers.now.get(key)

   override protected def selectParams(el: HTMLElement): SelectizeConfigBuilder =
     SelectizeConfig
       .delimiter("|")
       .persist(false)
       .valueField("id")
       .labelField("title")
       .searchField("title")
       .onType(typeHandler)
       .onItemAdd(itemAddHandler _)
       .onItemRemove(itemRemoveHandler _)
   /*  js.Dynamic.literal(
       delimiter = "|",
       persist = false,
       valueField = "id",
       labelField = "title",
       searchField = "title",
       onType = typeHandler  ,
       onItemAdd = itemAddHandler _,
       onItemRemove =  itemRemoveHandler _,
       options = js.Array()
     )
   */

   override def itemAddHandler(value:String, item:js.Dynamic): Unit =
   {
     val filters =  this.modifiers.now
     val v = this.parseRDF(value)
     this.propertyFilterOption match {
       case Some(filter:Filters.ValueFilter)=>
         if(!filter.values.contains(v)) this.modifiers() = filters + (key->filter.add(v))


       case Some(other)=> dom.console.log("do not add other filters yet")
       case None=>this.modifiers() = filters + (key->new Filters.ValueFilter(key,Set(v)))
     }
   }


   override def itemRemoveHandler(value:String): Unit = {
     val v = this.parseRDF(value)
     this.propertyFilterOption match {
       case Some(filter:Filters.ValueFilter)=>
         val f = filter.remove(v)
         if(f.isEmpty) this.modifiers() = this.modifiers.now-key else this.modifiers() = this.modifiers.now + (key->f)
       case Some(other)=>
         dom.console.log("do not remove other filters yet")
       case None=>
     }
   }


   def fillValues(fls:Map[IRI,Filters.Filter]):this.type = {
     sel.clearOptions()
     fls.get(key) match {
       case Some(f:ValueFilter)=>
         f.values.foreach{v=>
           dom.console.log("filled = "+v.stringValue)
           sel.addOption(this.makeOption(v))
           sel.addItem(v.stringValue)

         }
       case _ =>
     }
     this
   }

   //protected def makeOption(v:String): js.Dynamic =  js.Dynamic.literal( id = v, title = v.label)


 }
