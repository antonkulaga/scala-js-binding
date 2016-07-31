package org.denigma.binding.views

import org.denigma.binding.extensions._
import rx._

import scala.collection.immutable._

trait UpdatableView[Value] extends BasicView
{
  def update(value: Value): this.type

  //def dirty: Rx[Boolean]
}

sealed trait BasicCollectionMapView extends CollectionView {

  type Key

  type Value

  override type Item = Key

  def updates: Rx[MapUpdate[Key, Value]]

  override protected def subscribeUpdates() = {
    template.hide()
    //this.items.now.foreach(i => this.addItemView(i, this.newItemView(i)) ) //initialization of views
    updates.onChange(upd=>{
      upd.added.foreach{
        case (key, value)=>
          val n = newItemView(key, value)
          this.addItemView(key, n)
      }
      upd.removed.foreach{ case (key, value ) => removeItemView(key)}
      upd.updated.foreach{ case( key, (old, current))=> this.updateView(itemViews.now(key), key, old, current)}
    })
  }

  def updateView(view: ItemView, key: Key, old: Value, current: Value): Unit

  def newItemView(key: Key, value: Value): ItemView
}

trait CollectionMapView extends BasicCollectionMapView{
  def items: Rx[Map[Key, Value]]

  lazy val updates: Rx[MapUpdate[Key, Value]] = items.updates

  override protected def subscribeUpdates() = {
    super.subscribeUpdates()
    for ( (key, value) <- items.now) {
      val n = newItemView(key, value)
      this.addItemView(key, n)
    }
  }
}

trait CollectionSortedMapView extends BasicCollectionMapView{
  def items: Rx[SortedMap[Key, Value]]

  lazy val updates: Rx[MapUpdate[Key, Value]] = items.updates

  override protected def subscribeUpdates() = {
    super.subscribeUpdates()
    for ( (key, value) <- items.now) {
      val n = newItemView(key, value)
      this.addItemView(key, n)
    }
  }
}