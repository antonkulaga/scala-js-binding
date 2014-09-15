package org.denigma.semantic.storages

import org.denigma.binding.extensions.sq
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.Res
import org.scalax.semweb.shex.Model

import scala.concurrent.Future


/**
 * Provides features to do storage operations with models
 */
trait SimpleStorage
{
  type MyModel<:Model



  def add(MyModel:MyModel): Future[XMLHttpRequest]
  def read(red:Res):Future[MyModel]
  def delete(MyModel:MyModel): Future[XMLHttpRequest]
  def delete(id:Res): Future[XMLHttpRequest]
  def update(MyModel:MyModel): Future[XMLHttpRequest]

  def all():Future[List[MyModel]]

}


trait AjaxSimpleStorage extends SimpleStorage{

  implicit def registry:PicklerRegistry

  def path:String

  override def all() = sq.get[List[MyModel]](sq.withHost(path))

  override def update(MyModel: MyModel): Future[XMLHttpRequest] = sq.put(sq.withHost(s"$path/update"),MyModel)

  override def delete(MyModel: MyModel): Future[XMLHttpRequest] = sq.delete[MyModel](sq.withHost(s"$path/delete"),MyModel)

  override def delete(id: Res): Future[XMLHttpRequest] = sq.put[Res](sq.withHost(s"$path/delete/id"),id)

  override def read(id: Res): Future[MyModel] = sq.post[Res,MyModel](sq.withHost(s"$path/id"),id)

  override def add(MyModel: MyModel): Future[XMLHttpRequest] = sq.put(sq.withHost(s"$path/add"),MyModel)
}