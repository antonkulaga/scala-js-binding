package org.denigma.controls.general

import org.denigma.binding.extensions.sq
import org.denigma.binding.models.MenuItem
import org.denigma.binding.views.collections.MapCollectionView
import org.denigma.semantic.storages.SimpleStorage
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, XMLHttpRequest}
import org.scalax.semweb.rdf.Res
import prickle.{Pickle, Unpickle}
import rx._

import scala.collection.immutable.{List, Map}
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Try, Failure, Success}

abstract class EditableMenuView(name:String,el:HTMLElement, params:Map[String,Any] = Map.empty) extends AjaxMenuView(name,el,params)
{
  self =>

  val editable = params.get("editable").fold(false){
    case b:Boolean=>b
    case _=>false
  }

}

abstract class AjaxMenuView(override val name:String,el:HTMLElement, params:Map[String,Any] = Map.empty)
  extends MapCollectionView(el,params) {
  self =>

  /**
   * Path that is used for loading menu
   */
  val path = params.get("path").fold("/menu/")(_.toString)
  val editMode = Var(params.get("editable").fold(false)({
    case bool: Boolean => bool
    case _ => false
  }))

  object storage extends SimpleStorage  {
    import org.denigma.binding.composites.BindingComposites
    import org.denigma.binding.composites.BindingComposites._
    def path: String = self.path

    override type MyModel = MenuItem

    override def update(model: MyModel): Future[XMLHttpRequest] = sq.tryPut(sq.withHost(s"$path/update"),model) (d=>Pickle.intoString(d))

    override def delete(model: MyModel): Future[XMLHttpRequest] = sq.tryDelete[MyModel](sq.withHost(s"$path/delete"),model) (d=>Pickle.intoString(d))

    override def delete(id: Res): Future[XMLHttpRequest] = sq.tryPut[Res](sq.withHost(s"$path/delete/id"),id) (d=>Pickle.intoString(d))

    override def read(id: Res): Future[MyModel] = sq.tryPost[Res,MyModel](sq.withHost(s"$path/id"),id)(d =>Pickle.intoString(d))(st =>Unpickle[MyModel].fromString(st))

    override def add(model: MyModel): Future[XMLHttpRequest] = sq.tryPut(sq.withHost(s"$path/add"),model)(d=>Pickle.intoString(d))

    override def all()= sq.tryGet(sq.withHost(s"$path/all")){  st=>  Unpickle[List[MyModel]].fromString(st)   }

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
    activateMacro()
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