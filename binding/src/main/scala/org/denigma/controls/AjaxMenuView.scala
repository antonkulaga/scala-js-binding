package org.denigma.controls

import org.scalajs.dom.HTMLElement
import rx._
import scala.collection.immutable.{Map, List}
import scala.util.{Failure, Success}
import org.scalajs.dom
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.denigma.storages.AjaxStorage
import org.denigma.views.lists.ListView
import scala.concurrent.Future
import org.denigma.binding.models.MenuItem
import org.denigma.binding.picklers.rp
import org.scalajs.spickling.PicklerRegistry


abstract class EditableMenuView(name:String,el:HTMLElement, params:Map[String,Any] = Map.empty) extends AjaxMenuView(name,el,params)
{
  self =>

  val editable = params.get("editable").fold(false){
    case b:Boolean=>b
    case _=>false
  }

}

abstract class AjaxMenuView(name:String,el:HTMLElement, params:Map[String,Any] = Map.empty) extends ListView(name,el,params) {
  self =>

  /**
   * Path that is used for loading menu
   */
  val path = params.get("path").fold("/menu/")(_.toString)
  val editMode = Var(params.get("editable").fold(false)({
    case bool: Boolean => bool
    case _ => false
  }))

  object storage extends AjaxStorage {
    override def path: String = self.path

    override type MyModel = MenuItem

    override implicit def registry: PicklerRegistry = rp
  }



  val menu: Var[List[MenuItem]] = Var {
    List.empty[MenuItem]
  }

  val items: Rx[List[Map[String, Any]]] = Rx {
    menu().map(ch => Map[String, Any]("label" -> ch.title, "uri" -> ch.uri.stringValue))
  }


  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el: HTMLElement) = {

    rp.registerPicklers()

    val futureMenu = storage.all()
    futureMenu.onComplete {
      case Success(data) =>
        this.menu() = data
        super.bindView(el)
      case Failure(m) => dom.console.error(s"Future data failure for view ${this.id} with exception: \n ${m.toString}")
    }
  }
}