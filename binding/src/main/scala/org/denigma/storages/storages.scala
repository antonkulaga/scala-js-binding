package org.denigma.storages

import scala.concurrent.Future
import org.denigma.extensions.sq
import org.scalax.semweb.rdf.Res
import org.scalajs.dom.XMLHttpRequest
import org.scalax.semweb.shex.Model


trait ReadWriteStorage{

  type MyModel<:Model
  
  def read(res:Res):Future[MyModel]
  //def write(model:MyModel):Future[Boolean]
  
}

/**
 * Provides features to do storage operations with models
 */
trait Storage extends ReadWriteStorage
{
 // type ReadResponse = XMLHttpRequest

  def add(MyModel:MyModel): Future[XMLHttpRequest]
  def read(red:Res):Future[MyModel]
  def delete(MyModel:MyModel): Future[XMLHttpRequest]
  def delete(id:Res): Future[XMLHttpRequest]
  def update(MyModel:MyModel): Future[XMLHttpRequest]

  def all():Future[List[MyModel]]

}


trait AjaxStorage extends Storage{

  def path:String


  override def all() = sq.get[List[MyModel]](sq.withHost(path))

  override def update(MyModel: MyModel): Future[XMLHttpRequest] = sq.put(sq.withHost(s"$path/update"),MyModel)

  override def delete(MyModel: MyModel): Future[XMLHttpRequest] = sq.delete[MyModel](sq.withHost(s"$path/delete"),MyModel)

  override def delete(id: Res): Future[XMLHttpRequest] = sq.put[Res](sq.withHost(s"$path/delete/id"),id)

  override def read(id: Res): Future[MyModel] = sq.post[Res,MyModel](sq.withHost(s"$path/id"),id)

  override def add(MyModel: MyModel): Future[XMLHttpRequest] = sq.put(sq.withHost(s"$path/add"),MyModel)
}