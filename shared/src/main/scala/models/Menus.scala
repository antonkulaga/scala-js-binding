package models

import org.scalax.semweb.rdf.IRI


//object Menu {
//  def apply(uri:IRI, label:String, child:MenuItem*): Menu = {
//    new Menu(uri,label,child:_*)
//  }
//
//}

case class TestMenu(uri:IRI,title:String)

//extends MenuItemLike

case class Menu(uri:IRI,title:String, children: List[MenuItem]) extends MenuItemLike

case class MenuItem(uri:IRI,title:String) extends MenuItemLike

trait MenuItemLike{
  val uri:IRI
  val title:String
}