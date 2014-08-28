package org.denigma.binding.frontend.slides

import org.denigma.binding.extensions._
import org.denigma.binding.views
import org.denigma.controls.editors.editors
import org.denigma.controls.general.EditModelView
import org.denigma.controls.semantic.AjaxLoadView
import org.scalajs.dom
import org.scalajs.dom._
import rx._

import scala.collection.immutable.Map
import scala.scalajs.js.Dynamic.{global => g, newInstance => jsnew}
import scalatags.Text.Tag

class PageEditView(val elem:HTMLElement,val params:Map[String,Any]) extends AjaxLoadView with EditModelView
{


  this.saveClick.takeIf(dirty).handler{
    //dom.console.log("it should be saved right now")
    this.saveModel()
  }


  this.toggleClick.handler{
    this.editMode() = !this.editMode.now
  }

  val editor = Var("ckeditor")

  //val editor = Var("codemirror")

  //val edChanges = editor.zip


  override def bindEditable(el:HTMLElement,key:String) = {
    this.bindRx(key,el,this.editMode){ (el,model)=>
      el.contentEditable = editMode().toString
      dom.console.log(editor.now)
      if(editMode.now) editors.on(el,this)(editor.now) else editors.offAll(el,this)

    }
  }

  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  lazy val textEvents: Map[String, rx.Var[TextEvent]] = this.extractTextEvents(this)

  lazy val mouseEvents: Map[String, rx.Var[dom.MouseEvent]] = this.extractMouseEvents(this)

  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)
}
