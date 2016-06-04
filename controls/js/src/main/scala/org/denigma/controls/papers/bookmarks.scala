package org.denigma.controls.papers

import org.denigma.binding.binders.Events
import org.denigma.binding.views.BindableView
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.{Element, HTMLElement, Selection}
import rx.{Rx, Var}
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.concurrent.Future
import scala.scalajs.js
import org.denigma.binding

case class Bookmark(paper: String, page: Int, selections: List[TextLayerSelection] = List.empty)

/*trait Bookmark{
  def paper: String
  def page: Int
  def selections: List[Selection]
}*/

//case class SimpleBookmark(paper: String, page: Int, selection: String = "") extends Bookmark


class BookmarkView(val elem: Element, val bookmark: Rx[Bookmark], location: Var[Bookmark]) extends BindableView {
  val go: Var[MouseEvent] = Var(Events.createMouseEvent())
  go.triggerLater{
    val sel = bookmark.now.selections
    println(sel.mkString("\n"))
    js.debugger()
    location() = bookmark.now
  }
  val paper = bookmark.map(_.paper)
  val page = bookmark.map(_.page)
  //val text = bookmark.map(_.selection.text)
  val text = Var("")//bookmark.map(_.selections.foldLeft("")((acc, el)=> acc +"\n"+ el.text))

}