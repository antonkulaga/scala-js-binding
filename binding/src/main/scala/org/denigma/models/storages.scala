package org.denigma.models

import org.denigma.views.OrganizedView
import scala.concurrent.Future
import org.scalajs.dom.extensions._
import org.denigma.extensions.sq
import org.scalax.semweb.rdf.Res
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.dom.XMLHttpRequest


trait Storage
{
  type Value //<:Model

  def add(value:Value): Future[XMLHttpRequest]
  def read(id:Res):Future[Value]
  def delete(value:Value): Future[XMLHttpRequest]
  def delete(id:Res): Future[XMLHttpRequest]
  def update(value:Value): Future[XMLHttpRequest]

  def all():Future[List[Value]]

}

trait AjaxStorage extends Storage{

  def path:String


  override def all() = sq.get[List[Value]](sq.withHost(path))

  override def update(value: Value): Future[XMLHttpRequest] = sq.put(sq.withHost(s"$path/update"),value)

  override def delete(value: Value): Future[XMLHttpRequest] = sq.delete[Value](sq.withHost(s"$path/delete"),value)

  override def delete(id: Res): Future[XMLHttpRequest] = sq.put[Res](sq.withHost(s"$path/delete/id"),id)

  override def read(id: Res): Future[Value] = sq.post[Res,Value](sq.withHost(s"$path/id"),id)

  override def add(value: Value): Future[XMLHttpRequest] = sq.put(sq.withHost(s"$path/add"),value)
}