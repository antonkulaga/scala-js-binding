package org.denigma.binding.views

import org.denigma.binding.extensions._
import rx._

import scala.collection.immutable._

trait UpdatableView[Value] extends BasicView
{
  def update(value: Value): this.type

  //def dirty: Rx[Boolean]
}

trait ItemsMapView extends CollectionView{

  type Value

  type Key = Item

  override type ItemView <: UpdatableView[Value]

  def items: Rx[Map[Key, Value]]


  lazy val updates: Rx[MapUpdate[Key, Value]] = items.updates

  override protected def subscribeUpdates() = {
    template.hide()
    //this.items.now.foreach(i => this.addItemView(i, this.newItemView(i)) ) //initialization of views
    updates.onChange(upd=>{
      upd.added.foreach{
        case (key, value)=>
          val n = newItemView(key)
          n.update(value)
          this.addItemView(key, n)
      }
      upd.removed.foreach{ case (key, value ) => removeItemView(key)}
      upd.updated.foreach{ case( key, (old, current))=> itemViews.now(key).update(current)}
    })
    for ( (key, value) <- items.now) {
      val n = newItemView(key)
      n.update(value)
      this.addItemView(key, n)
    }
  }
}