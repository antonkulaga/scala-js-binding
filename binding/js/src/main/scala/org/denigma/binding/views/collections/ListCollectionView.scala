package org.denigma.binding.views.collections

import org.denigma.binding.binders.collections.KeyValue
import org.denigma.binding.binders.collections.KeyValue.StringBinder
import org.denigma.binding.views.BindableView


abstract class ListCollectionView extends BindableView  with CollectionView
{
//val key = params.get("items").getOrElse("items").toString

val disp = elem.style.display

override type Item = (String,String)
override type ItemView = BindableView

  def newItem(item:Item):ItemView = {
    //debug("ITEM="+item.toString())
    this.constructItemView(item){  (el,mp)=>  BindableView(el,mp).withBinder(new StringBinder(_,item._1,item._2)) }
  }


}