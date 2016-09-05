package org.denigma.controls.code

import org.denigma.binding.binders.{ReactiveBinder, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.macroses._
import org.denigma.binding.views.BindableView
import org.denigma.codemirror.extensions.EditorConfig
import org.denigma.codemirror.{CodeMirror, Editor}
import org.querki.jquery._
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.raw.{HTMLElement, HTMLTextAreaElement}
import rx._
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.collection.immutable.Map

class CodeBinder[View <: BindableView](view: View, recover: Option[ReactiveBinder] = None)
                                    (implicit
                                     mpMap: MapRxMap[View], mpTag: TagRxMap[View],
                                     mpString: StringRxMap[View], mpBool: BooleanRxMap[View],
                                     mpDouble: DoubleRxMap[View], mpInt: IntRxMap[View],
                                     mpEvent: EventMap[View], mpMouse: MouseEventMap[View],
                                     mpText: TextEventMap[View], mpKey: KeyEventMap[View],
                                     mpUI: UIEventMap[View], mpWheel: WheelEventMap[View],
                                     mpFocus: FocusEventMap[View],  mpDrag: DragEventMap[View]
                                      )
extends GeneralBinder[View](view, recover)(
  mpMap, mpTag,
  mpString, mpBool,
  mpDouble, mpInt,
  mpEvent, mpMouse,
  mpText, mpKey,
  mpUI, mpWheel, mpFocus, mpDrag)
{
  val modes: Map[String, String] = Map(
    "html"->"htmlmixed",
    "htmlmixed"->"htmlmixed",
    "clike"->"clike",
    "sparql"->"x-sparql-query",
    "scala"->"text/x-scala",
    "rust"->"text/x-rustsrc",
    "r"->"text/x-rsrc"
  )

  var editors = Map.empty[HTMLElement, Editor]

  def allStrings: String =strings.mapValues(str=>str.now).mkString("\n") //for debugging

  def allStringsKeys: String =strings.map{case (key, value) => key}.mkString("\n")  //for debugging

 override def elementPartial(el: Element, ats: Map[String, String]) = super.elementPartial(el, ats).orElse(codePartial(el, ats))

  def codePartial(el: Element, ats: Map[String, String]): PartialFunction[(String, String), Unit] = {
    case ("bind-code" | "code", value) => ats.get("mode") match {
      case Some(m) =>  this.bindCode(el, value, m)
      case None=>
        if (el.hasChildNodes())  el.firstChild match {
            case el: HTMLElement=>
              val lang = el.className.split(" ")
                .collectFirst{
                  case cl if cl.contains("language-") =>cl.replace("language-","")
                }.getOrElse("htmlmixed")
               bindCode(el, value, lang)
            }
        else
          this.bindCode(el, value, "htmlmixed")
    }
    case (st, value) if st.startsWith("bind-code-") | st.startsWith("code-") =>
      val mname = st.replace("bind-", "").replace("code-", "")
      modes.get(mname) match {
      case Some(m) => this.bindCode(el, value, m)
      case None => dom.console.error(s"language mode $mname")
    }
  }


  def makeEditor(area: HTMLTextAreaElement, textValue: String, codeMode: String, readOnly: Boolean = false): Editor = {
    val params = EditorConfig
      .mode(codeMode)
      .lineNumbers(true)
      .value(textValue)
      .readOnly(readOnly)
      .viewportMargin(Integer.MAX_VALUE)
    CodeMirror.fromTextArea(area, params)
  }

  def onChange(code: Var[String])(ed: Editor): Unit =
  {
    val v =  ed.getDoc().getValue()
    if(code.now!=v)  code() = v
  }

  def bindCode(el: Element, value: String, mode: String): Unit = this.strings.get(value) match {
    case Some(str: Var[String])=>
      if(str.now == "") codeFromElement(el, str)
      this.makeCode(el, str, mode)

    case Some(str)=> this.makeCode(el, str, mode)
    case None=>
      dom.console.error(s"cannot find code stringRx $value in ${view.id} \n" +
        s"all string keys are:\n ${allStringsKeys}\n" +
        s"html element is ${view.elem.outerHTML}")
  }

  protected def codeFromElement(el: Element, str: Var[String]): Unit = {
    if (el.innerHTML == "" || el.children.length == 0) {
      val t = $(el).text()
      str.set(t)
    }
    else el.firstChild match {
      case e: HTMLElement if e.tagName == "code" =>
        val t = if (e.children.length > 0) e.innerHTML else $(e).text()
        el.innerHTML = ""
        str.set(t)

      case _ =>
        val t = el.innerHTML
        el.innerHTML = ""
        str.set(t)
    }
  }

  protected def textArea2Code(area: HTMLTextAreaElement, str: Rx[String], mode: String): Unit  =  this.editors.get(area) match {
      case Some(ed) =>  ed.getDoc().setValue(str.now)
      case None=>
        val ed = this.makeEditor(area, str.now, mode)
        this.editors = this.editors + (area -> ed)
        if (str.now!="") ed.getDoc().setValue(str.now)
        str match {
          case s: Var[String] => ed.on("change", onChange(s) _)
          case _ =>  //dom.console.info(s"${str.now} is not reactive Var in ${view.id}")
        }
        str.onChange(s => {
          val d = ed.getDoc()
          if(d.getValue() != s) d.setValue(s)
        })
    }


  def makeCode(el: Element, str: Rx[String], mode: String): Unit = el match {
    case area: HTMLTextAreaElement => textArea2Code(area, str, mode)
    case other=>
      dom.document.createElement("textarea") match {
        case area: HTMLTextAreaElement=>
          area.value = el.innerHTML
          el.innerHTML=""
          el.appendChild(area)
          textArea2Code(area, str, mode)
        case _ => dom.console.error(s"cannot create a textarea for ${el.outerHTML} with ${str}")
      }
  }


}
