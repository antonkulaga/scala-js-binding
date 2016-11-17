package org.denigma.preview.data

import org.denigma.binding.macroses._
import org.denigma.controls.models.TextOption

import scala.collection.immutable.{SortedSet, _}
case object TestOptions{

  val states: Vector[(String, String)] = {
    val mp = CSV.toDataFrame("preview/data/state_table.csv")
    mp.name.zip(mp.abbreviation)
  }

  //println("STATES = ")
  //states.foreach(st => println(st))

  lazy val data = states.zipWithIndex.map{case ((label, value), index) => TextOption(value, label, position = index)}
  lazy val sortedOptions = SortedSet(data: _*)

  lazy val options: scala.collection.immutable.Seq[TextOption] = sortedOptions.toList

  def search(input: String): Seq[TextOption] = {
    val lc = input.toLowerCase
    options.filter(o=>o.label.toLowerCase.contains(lc) || o.value.toLowerCase.contains(lc))
  }

  val items = sortedOptions.take(3)

}

