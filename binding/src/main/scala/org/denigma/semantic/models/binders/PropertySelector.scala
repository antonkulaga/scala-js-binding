package org.denigma.semantic.models.binders

import org.denigma.binding.extensions._
import org.denigma.semantic.rdf.{Selector, ModelInside}
import org.scalajs.dom.HTMLElement
import org.scalajs.jquery._
import org.scalax.semweb.rdf._
import rx.Var
import org.denigma.binding.extensions._

import scala.scalajs.js

/**
 * Selects property from the model
 * @param el
 * @param key
 * @param model
 * @param typeHandler
 */
class PropertySelector(val el:HTMLElement,val key:IRI,val model:Var[ModelInside], typeHandler:(String)=>Unit) extends Selector
{

  val sel: js.Dynamic = jQuery(el).dyn.selectize(selectParams(el))

  protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      delimiter = "|",
      persist = false,
      valueField = "id",
      labelField = "title",
      searchField = "title",
      onType = typeHandler  ,
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      options = makeOptions()
    )
  }

  def makeOptions():js.Array[js.Dynamic] =
    this.model.now.current.properties.get(key) match {
      case Some(iris)=>
        val o: List[js.Dynamic] = iris.map(i=> makeOption(i)).toList
        js.Array( o:_* )
      case None=> js.Array()
    }


  protected def itemAddHandler(value:String, item:js.Any): Unit = {
    //dom.console.log("added = "+value)
    val mod = model.now
    mod.current.properties.get(key) match {
      case None=> model() = mod.add(key,parseRDF(value))
      case Some(ps) => if(!ps.exists(p=>p.stringValue==value)) model() = mod.add(key,parseRDF(value))
    }
  }

  protected def itemRemoveHandler(value:String): Unit = {
    val mod =  model.now
    val remove: Option[Set[RDFValue]] =mod.current.properties.get(key).map{ps=>  ps.collect{case p if p.stringValue==value=>p}    }
    if(remove.nonEmpty) {
      val n = this.parseRDF(value)
      val s1 = mod.current.properties(key).size
      val md = mod.delete(key,n)
      val s2 = md.current.properties(key).size
      //dom.console.log(s"s1 = $s1 | s2 = $s2")
      model() = md
    }
  }


   def fillValues(model: ModelInside):this.type = {
    val ss= this.selectizeFrom(el)
    ss.clearOptions()
    model.current.properties.get(key).foreach{ps=>
      ps.foreach{p=>
        ss.addOption(this.makeOption(p))
        ss.addItem(p.stringValue)
      }
    }
    this
  }

}