package org.denigma.controls.papers

import org.denigma.binding.binders.Events
import org.denigma.binding.views.BindableView
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.{Selection, Element}
import rx.{Var, Rx}
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.concurrent.Future

object Bookmark{

  def apply(paper: String, page: Int, selection: String): Bookmark = Bookmark(paper, page, List(TextSelection(selection)))
}

case class Bookmark(paper: String, page: Int, selections: List[TextSelection] = List.empty)

/*trait Bookmark{
  def paper: String
  def page: Int
  def selections: List[Selection]
}*/

//case class SimpleBookmark(paper: String, page: Int, selection: String = "") extends Bookmark


class BookmarkView(val elem: Element, val bookmark: Rx[Bookmark], location:Var[Bookmark]) extends BindableView {
  val go: Var[MouseEvent] = Var(Events.createMouseEvent())
  go.triggerLater{
    location() = bookmark.now
  }

  val paper = bookmark.map(_.paper)
  val page = bookmark.map(_.page)
  val text = bookmark.map(_.selections.foldLeft("")((acc, el)=> acc + el.text))

}