package org.denigma.selectors

import org.scalajs.dom.raw.HTMLElement
import rx.core.Var

import scala.scalajs.js
import scala.scalajs.js.Dynamic
import org.denigma.selectize._

object SimpleSelector {
  def apply(el:HTMLElement,options:Seq[String]) = new SimpleSelector(el,options.map(s=>(s,s)))
}

/**
 * Not yet ready
 * @param el
 * @param options
 */
class SimpleSelector(val el:HTMLElement,options:Seq[(String,String)]) extends Selector //with Escaper
{

  val selected = Var("")


  protected def makeOption(vid: String, title: String): SelectOption = SelectOption(vid,title)

  protected def selectParams(el: HTMLElement):SelectizeConfigBuilder=
    SelectizeConfig
      .delimiter("|")
      .persist(false)
      .valueField("id")
      .labelField("title")
      .searchField("title")
      .onItemAdd(itemAddHandler _)
      .onItemRemove(itemRemoveHandler _)
      .options(makeOptions():js.Array[SelectOption])
      .create(false)


  /*  js.Dynamic.literal(
      delimiter = "|",
      persist = false,
      valueField = "id",
      labelField = "title",
      searchField = "title",
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      options = this.makeOptions(),
      create = false
    )
  */




  def makeOptions(): js.Array[SelectOption] = js.Array(options.map{case (i,t)=>makeOption(i,t)}:_*)


  override protected def itemRemoveHandler(value: String): Unit = {
    selected() = selected.now.replace(value,"")
  }

  override protected def itemAddHandler(value: String, item: Dynamic): Unit = {
    selected() = value
  }
}
