package org.denigma.binding.models

import org.scalax.semweb.rdf.{Res, IRI}

case class TestMenu(uri:IRI,title:String)

//extends MenuItemLike

case class Menu(uri:IRI,title:String, children: List[MenuItem]) extends MenuItemLike

case class MenuItem(uri:IRI,title:String) extends MenuItemLike

trait MenuItemLike extends Model
{
  val uri:IRI
  val title:String

  override def id:Res = uri
}