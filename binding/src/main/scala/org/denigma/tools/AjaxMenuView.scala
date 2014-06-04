package org.denigma.tools

import org.scalajs.dom.HTMLElement
import org.denigma.views.ListView
import rx._
import org.denigma.models.AjaxStorage
import org.denigma.binding.models.{rp, MenuItem}
import scala.collection.immutable.{Map, List}
import scala.util.{Failure, Success}
import org.scalajs.dom
import scalajs.concurrent.JSExecutionContext.Implicits.queue

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

    override type Value = MenuItem
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