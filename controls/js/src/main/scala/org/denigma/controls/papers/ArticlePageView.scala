package org.denigma.controls.papers

import org.denigma.binding.extensions._
import org.denigma.controls.commons.OptimizedView
import org.denigma.pdf.extensions.{Page, PageRenderer}
import org.scalajs.dom
import org.scalajs.dom.Node
import org.scalajs.dom.ext._
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{Element, _}
import rx._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

import scala.collection.immutable._
import scala.concurrent.Promise
import scala.util.{Failure, Success}

class ArticlePageView(val elem: HTMLElement,
                      val num: Int,
                      val page: Page,
                      val scale: Rx[Double]
                     )  extends PageView with OptimizedView {
 val name = Var("page_"+num)
 val title = Var("page_"+num)

  def textLayerSelection(sel: Selection): List[TextLayerSelection] = {
    if(sel.anchorNode.isInside(textDiv) || sel.focusNode.isInside(textDiv)) sel.map{ range =>
      TextLayerSelection.fromRange("", range)}.toList
    else List.empty[TextLayerSelection]
  }

  protected var canvasParent: Option[Element] = None
  protected var textDivParent: Option[Element] = None
  lazy val emptyCanvas = dom.document.createElement("canvas").asInstanceOf[Canvas]


  override lazy val canvas = {
    val result = elem.getElementsByTagName("canvas").collectFirst{
      case canv: Canvas => canv
    }.orElse(elem.children.collectFirst {
        case canv: Canvas => canv
      }).get
    canvasParent = Option(result.parentElement)
    result
  } //unsafe

  override lazy val textDiv = {
    val result = elem.getElementsByClassName("textLayer").collectFirst{
      case el: HTMLDivElement => el
    }.orElse(elem.children.collectFirst {
      case canv: HTMLDivElement => canv
    }).get
    textDivParent = Option(result.parentElement)
    result
  } //unsafe

  lazy val pageRenderer = new PageRenderer(page)

  def makeVisible() = {
    canvasParent match {
      case Some(p) => if(!p.children.contains(canvas)) {
        if(p.children.contains(emptyCanvas)) {
          p.insertBefore(canvas, emptyCanvas)
          p.removeChild(emptyCanvas)
        } else p.appendChild(canvas)
      }
      case None =>
        dom.console.error("cannot find textdiv parent!")
    }
    textDivParent match {
      case Some(p)  =>
        if(!p.children.contains(textDiv)) {
          p.appendChild(textDiv)
        }

      case None =>
        dom.console.error("cannot find textdiv parent!")
    }
    pageRenderer.adjustSize(elem, emptyCanvas, textDiv, scale.now)
  }

  def hide() = {
    canvasParent match {
      case Some(p)=>
        if(p.children.contains(canvas)) {
          p.insertBefore(emptyCanvas, canvas)
          p.removeChild(canvas)
        } else p.appendChild(emptyCanvas)

      case None =>
        dom.console.error("cannot find textdiv parent!")
    }

    textDivParent match {
      case Some(p)  =>
        if(p.children.contains(textDiv)) p.removeChild(textDiv)
      case None =>
        dom.console.error("cannot find textdiv parent!")
    }
  }


  type RenderResults = (Canvas, HTMLElement, List[(String, Node)])

  protected lazy val renderPromise: Promise[RenderResults] = Promise[RenderResults]

  lazy val renderedPage = renderPromise.future

  override protected def renderPage(page: Page): Unit = {
    pageRenderer.adjustSize(elem, emptyCanvas, textDiv, scale.now)
    pageRenderer.render(canvas, textDiv, scale.now).onComplete{
      case Success(result)=>
        if(checkVisibility()) makeVisible() else hide()
        textDiv.innerHTML = ""
        for {(str, node) <- result._3} {textDiv.appendChild(node) }
        renderPromise.success(result)

      case Failure(th) =>
        dom.console.error(s"cannot load the text layer for page ${page.num} because of the ${th}")
        renderPromise.failure(th)
    }
  }

  def getSpans(sel: TextLayerSelection): List[Element] = sel.selectTokenSpans(textDiv)


  def unhighlight(sel: TextLayerSelection ) = {
    val spans = getSpans(sel)
    //dom.console.log(s"DE_HIGHLIGHT fromChunk ${sel.fromChunk} toChunk ${sel.toChunk} fromToken ${sel.fromToken} toToken ${sel.toToken} spans length = ${spans.length}")
    spans.foreach { sp =>
      if (sp.classList.contains("highlight"))
        sp.classList.remove("highlight")
    }
  }

  def highlight(sel: TextLayerSelection) = {
    //println("chunks are: ")
    val spans = getSpans(sel)
    //dom.console.log(s"HIGHLIGHT fromChunk ${sel.fromChunk} toChunk ${sel.toChunk} fromToken ${sel.fromToken} toToken ${sel.toToken} spans length = ${spans.length}")
    spans.foreach{ sp=> if(!sp.classList.contains("highlight")) sp.classList.add("highlight") }
  }


}
