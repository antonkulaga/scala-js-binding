package org.denigma.views.core

import scala.util.Try
import scala.concurrent.Future
import org.scalajs.dom.HTMLElement


/**
 * View that fetches remote data
 */
trait RemoteView extends OrganizedView with Remote{


  def path:String

  /**
   * Extracts future data with typeclass pattern
   * @return
   */
  def futureData(implicit getFuture:FromFuture): Future[RemoteData] = getFuture(path)


}


trait Remote{
  type RemoteData

  type FromFuture = String=>Future[RemoteData]

}