package org.denigma.preview

import org.denigma.binding.views.ItemsSetView
import rx.core.{Rx, Var}
import rx.ops._
import org.denigma.binding.extensions._

/*
class SemanticSidebarView extends ItemsSetView{

  def my(str:String) = str+"_of_"+this.id //to name Vars for debugging purposes

  val input: Var[String] = Var("","input_of_"+this.id)

  val positionShift = Var(0,my("positionShift"))

  val position: Rx[Int] = Rx{
    items().size+positionShift()
  }

  val order: Rx[String] = position.map(_.toString)

  protected def moveLeft() = if(position.now > -1) positionShift.set(positionShift.now-1)
  protected def moveRight() = if(position.now<items.now.size) positionShift.set(positionShift.now+1)
  //val items: Var[Seq[Var[TextSelection]]] = Var(testData.take(3).zipWithIndex.map{ case (o,i)=> Var(o.copy(position=i)) }) //just for the sake of tests

}
*/
