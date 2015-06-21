package org.denigma.controls.binders

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.codemirror.extensions.EditorConfig
import org.denigma.codemirror.{CodeMirror, Editor, extensions}
import org.querki.jquery._
import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLElement, HTMLTextAreaElement}
import rx._

import scala.collection.immutable.Map

class CodeBinder(view:BindableView) extends GeneralBinder(view:BindableView)
{

  var editors = Map.empty[HTMLElement,Editor]

  override def bindProperties(el:HTMLElement,ats:Map[String, String]): Unit = for {
    (key, value) <- ats
  }{
    this.visibilityPartial(el,value)
      .orElse(this.classPartial(el,value))
      .orElse(this.codePartial(el,value,ats))
      .orElse(this.propertyPartial(el,key.toString,value))
      .orElse(this.upPartial(el,key.toString,value))
      .orElse(this.otherPartial)(key.toString)//key.toString is the most important!
  }

  def codePartial(el:HTMLElement,value:String,ats:Map[String, String]):PartialFunction[String,Unit] = {
    case "bind-code" | "code" => ats.get("mode") match {
      case Some(m)=>
        this.makeCode(el,value,m)
      case None=> this.makeCode(el,value,"htmlmixed")
    }

    case "bind-code-html" | "code-html" =>
      this.makeCode(el,value,"htmlmixed")

    case "bind-code-sparql" | "code-sparql" => makeCode(el,value,"application/x-sparql-query")

    case "bind-code-r" | "code-r" => makeCode(el,value,"r")

    case "bind-code-scala" | "code-scala" | "bind-code-clike" | "code-clike" => makeCode(el,value,"clike")

    case att if att.contains("bind-code-")=>
      dom.console.error(s"Unknown code mode ${}")
  }

  def makeEditor(area:HTMLTextAreaElement,textValue:String,codeMode:String,readOnly:Boolean = false) = {
    val params = EditorConfig
      .mode(codeMode)
      .lineNumbers(true)
      .value(textValue)
      .readOnly(readOnly)
      .viewportMargin(Double.PositiveInfinity)
    CodeMirror.fromTextArea(area,params)
  }

  def onChange(code:Var[String])(ed:Editor)
  {
    val v =  ed.getDoc().getValue()
    if(code.now!=v)  code() = v
  }

  def makeCode(el:HTMLElement,value:String,mode:String):Unit = this.strings.get(value) match {
    case Some(str:Var[String])=>
      if(str.now=="")
      {
        if(el.innerHTML!=""){
          val t = $(el).text()
          str() = t
          el.innerHTML = ""
        }
      }
      this.makeCode(el,str,mode)

    case Some(str)=> this.makeCode(el,str,mode)
    case None=>  dom.console.error(s"cannot find code string $value in $id")
  }


  def makeCode(el:HTMLElement, str:Rx[String], mode:String):Unit = el match {
    case area: HTMLTextAreaElement =>
      this.editors.get(area) match {
        case Some(ed) =>
          ed.getDoc().setValue(str.now)

        case None =>
          val ed = this.makeEditor(area, str.now, mode)
          this.editors = this.editors + (area -> ed)
          if(str.now!="") ed.getDoc().setValue(str.now)
          str match {
            case s: Var[String] => ed.on("change", onChange(s) _)
            case _ =>  dom.console.info(s"$str.now is not reactive Var in $id")
          }
          str.handler{
            val d = ed.getDoc()
            if(d.getValue()!=str.now) d.setValue(str.now)
          }

      }

    case _=>  dom.console.error(s"cannot find code string ${str.now} in $id")
  }




}
