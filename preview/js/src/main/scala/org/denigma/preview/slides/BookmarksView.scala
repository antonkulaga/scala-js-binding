package org.denigma.preview.slides

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views.ItemsSeqView
import org.denigma.controls.code.CodeBinder
import org.denigma.controls.papers._
import org.querki.jquery.$
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw._
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._
import org.scalajs.dom.ext._

import scala.annotation.tailrec
import scala.scalajs.js

/**
  * Created by antonkulaga on 2/14/16.
  */
class BookmarksView(val elem: Element, location: Var[Bookmark], textLayer: Element) extends ItemsSeqView {

  override type Item = Var[Bookmark]

  override type ItemView = BookmarkView

  override val items: Var[List[Item]] = Var(List.empty)

  val paper = location.map(_.paper)
  val page = location.map(_.page)

  val selections = Var(List.empty[Range])

  val currentSelection = selections.map{ case sel =>
    sel.foldLeft("")((acc, el)=>acc + "\n" + el.cloneContents().textContent)
  }

  val lastSelections: Var[List[TextSelection]] = Var(List.empty[TextSelection])

  val comments = Rx{
    "\n#^ :in_paper "+paper() +
    "\n#^ :on_page "+ page() + lastSelections.now.foldLeft(""){
      case (acc, el) => acc + "\n#^ :has_text " + el.text
    }
  }

  override def newItemView(item: Item): ItemView  = this.constructItemView(item){
    case (el, mp) =>
      new BookmarkView(el, item, location).withBinder(new CodeBinder(_))
  }


  val addSelection = Var(Events.createMouseEvent())

  def addSelectionHandler(event: MouseEvent) = {
      val book = location.now
      val mark = Bookmark(book.paper, book.page, lastSelections.now)
      val item = Var(mark)
      //println(s"NUMBER OF DUPLICATES: "+items.now.count(_==item))
      //println(s"NUMBER OF UNVAR DUPLICATES: "+items.now.count(_.now==item.now))
      if(!items.now.exists(_.now==mark)) items() = items.now ++ (item::Nil)
  }

  @tailrec final def inTextLayer(node: Node): Boolean = if(node == null) false
  else if (node.isEqualNode(textLayer) || textLayer == node || textLayer.isSameNode(node)) true
  else if(node.parentNode == null) false else inTextLayer(node.parentNode)

  protected def onSelectionChange(event: Event) = {
    val selection: Selection = dom.window.getSelection()
    val count = selection.rangeCount
     inTextLayer(selection.anchorNode) || inTextLayer(selection.focusNode)  match {
      case true =>
         if (count > 0) {
          selections() = {
            for{
              i <- 0 until count
              range = selection.getRangeAt(i)
            } yield range
          }.toList
          //val text = selections.foldLeft("")((acc, el)=>acc + "\n" + el.cloneContents().textContent)
          //currentSelection() = text
        }
      case false => //println(s"something else ${selection.anchorNode.textContent}") //do nothing
    }

  }

  protected def rangeToTextSelection(range: Range) = {
    val fragment = range.cloneContents()
    //val s = fragment.isInstanceOf[HTMLElement]
    //val txt = fragment.nodeValue
    val div = dom.document.createElement("div") //the trick to get inner html of the selection
    val nodes = fragment.childNodes.toList
    val txt = div.innerHTML
    js.debugger()
    nodes.foreach(div.appendChild)

    TextSelection(txt)
  }

  protected def fixSelection(event: Event): Unit = {
    //println("mouseleave")
    if(currentSelection.now != "") {
      lastSelections() = selections.now.map(rangeToTextSelection)
      selections() = List.empty
      //currentSelection() = ""
    }
  }

  override protected def subscribeUpdates() = {
    template.hide()
    this.items.now.foreach(i => this.addItemView(i, this.newItemView(i)))
    val upd = updates
    upd.onChange(upd => {
      println(s"change happenz!:\n+++++++++++++++++" +
        s"\nADDED: \n${upd.added.mkString("\n")}" +
        s"\nREMOVED: \n${upd.removed.mkString("\n")}" +
        s"\nMOVED: \n${upd.moved.mkString("\n")}" +
        s"\n ----------------------------")
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
      upd.moved.foreach(onMove)
    })
  }

/*
  lazy val codemirror = {
    this.binders.collectFirst{
      case cb: CodeBinder => cb.editors.head
    }.get //VERY UGLY AND BAD CODE
  }
*/

  override def bindView() = {
    super.bindView()
    dom.window.document.onselectionchange = onSelectionChange _
    addSelection.onChange(addSelectionHandler)
    textLayer.parentNode.addEventListener(Events.mouseleave, fixSelection _)
  }

}
