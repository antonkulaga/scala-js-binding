package org.denigma.binding.models

import org.denigma.semweb.rdf.{Res, IRI}
import org.denigma.semweb.shex.Model

case class TestMenu(uri:IRI,title:String)

//extends MenuItemLike

case class Menu(uri:IRI,title:String, children: List[MenuItem], icon:String="") extends MenuItemLike

case class MenuItem(uri:IRI,title:String, icon:String ="") extends MenuItemLike

trait MenuItemLike extends Model
{
  val uri:IRI
  val title:String
  val icon:String


  override def id:Res = uri
}