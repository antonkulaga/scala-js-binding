package org.denigma.controls.general

import org.denigma.binding.models.MenuItem
import org.denigma.binding.picklers.rp
import org.denigma.binding.storages.AjaxSimpleStorage
import org.denigma.binding.views.ListView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.spickling.PicklerRegistry
import rx._

import scala.collection.immutable.{List, Map}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

abstract class EditableMenuView(name:String,el:HTMLElement, params:Map[String,Any] = Map.empty) extends AjaxMenuView(name,el,params)
{
  self =>

  val editable = params.get("editable").fold(false){
    case b:Boolean=>b
    case _=>false
  }

}

abstract class AjaxMenuView(override val name:String,el:HTMLElement, params:Map[String,Any] = Map.empty) extends ListView(el,params) {
  self =>

  /**
   * Path that is used for loading menu
   */
  val path = params.get("path").fold("/menu/")(_.toString)
  val editMode = Var(params.get("editable").fold(false)({
    case bool: Boolean => bool
    case _ => false
  }))

  object storage extends AjaxSimpleStorage  {
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


//  override def bindView(el: HTMLElement) = {
//    super.bindView(el)
//    rp.registerPicklers()
//    val futureMenu = storage.all()
//    futureMenu.onComplete {
//      case Success(data) =>
//        this.menu() = data
//      case Failure(m) => dom.console.error(s"Future data failure for view ${this.id} with exception: \n ${m.toString}")
//    }
//  }

  //TODO: maybe dangerous!!!
  /**
   *
   * @param el html element to which view is binded
   */
  override def bindView(el:HTMLElement) = {
    this.bind(el)
    this.subscribeUpdates()
    val futureMenu = storage.all()
    futureMenu.onComplete {
      case Success(data) =>
        this.menu() = data
      case Failure(m) => dom.console.error(s"Future data failure for view ${this.id} with exception: \n ${m.toString}")
    }


  }
}