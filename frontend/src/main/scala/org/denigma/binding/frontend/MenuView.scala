package org.denigma.binding.frontend

import rx._
import models._
import org.scalajs.dom
import org.scalajs.dom._
import scala.collection.immutable._
import org.denigma.views._
import org.denigma.extensions._
import models.Menu
import scala.util.Success
import scala.util.Failure
import scalatags.HtmlTag
import models.MenuItem
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.scalax.semweb.rdf.IRI

object MenuView extends Remote{
  val menus = Map.empty[String,Menu]

  type RemoteData = Menu

  implicit val fromFuture:FromFuture = (str)=> {
    RegisterPicklers.registerPicklers()
    sq.get[Menu](sq.withHost("/menu/"+str))
  }

}

/**
 * Menu view, this view is devoted to displaying menus
 * @param el html element
 * @param params view params (if any)
 */
class MenuView(el:HTMLElement, params:Map[String,Any] = Map.empty) extends ListView("menu",el,params) with RemoteView
{
  import MenuView._

  type RemoteData = Menu

  val path = params.getOrError("domain").map(_.toString).get

  val menu: Var[Menu] = Var {
    //MenuView.testMenu
    Menu(IRI(s"http://${dom.window.location.host}"),dom.window.location.host,List.empty)
  }

  val items: Rx[List[Map[String, Any]]] = Rx {
    menu().children.map(ch=>Map[String,Any]("label"->ch.title,"uri"->ch.uri.stringValue))
  }


  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el:HTMLElement) = {
    val futureMenu = this.futureData
    futureMenu.onComplete{
      case Success(data)=>
        this.menu()=data
        super.bindView(el)
      case Failure(m)=>dom.console.error(s"Future data failuer for view ${this.id} with exception: \n ${m.toString}")
    }
  }

  override lazy val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override lazy val mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)

  override lazy val  lists: Map[String, Rx[scala.List[Map[String, Any]]]] = this.extractListRx(this)
}


