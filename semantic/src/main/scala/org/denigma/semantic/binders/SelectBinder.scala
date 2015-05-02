package org.denigma.semantic.binders
import org.denigma.binding.extensions._
import org.denigma.selectize.{Selectize, SelectizePlugin}
import org.denigma.selectors.{Escaper, SelectOption}
import org.scalajs.jquery.JQuery

import scala.Predef
import scalajs.js.isUndefined

import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.denigma.semweb.rdf.{IRI, RDFValue}
import rx.Var

import scala.collection.immutable.Map
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.{ThisFunction0, ThisFunction1}
import scala.util.{Failure, Success}

object SelectBinder extends BetterCreatePlugin("select_binder")

/**
 * 
 * @param pluginName
 */
class BetterCreatePlugin(pluginName:String) extends BetterDropdownPlugin(pluginName){

  override def pluginHandler(self:js.Dynamic,settings:js.Dynamic):Unit =
  {
    val dropDown:ThisFunction0[js.Dynamic,Unit] = positionDropdown _
    self.positionDropdown = dropDown
    val createItemHandler:js.Function1[Any,Boolean] = this.createItem(self.asInstanceOf[Selectize]) _
    self.createItem = createItemHandler
    //self.onKeyPress = onKeyPress(self) _


    //self.onKeyPress = js.eval(  """ function(e) {  alert("works"); 	}   """)
  }

  def createItem(self:Selectize)(triggerDropdown:Any)=  {
    val input: String =  if(self.$control_input==null) "" else self.$control_input.`val`().toString
    if(self.canCreate(input)){
      val caret = self.caretPos
      self.lock()
      val data:SelectOption =  if( !js.isUndefined(self.settings.createItem) &&   self.settings.createItem!=null
      ){
        val r = self.settings.createItem(input)
        //dom.console.error("OUTPUT = "+r)
        val fun = self.settings.createItem.asInstanceOf[js.Function1[String,SelectOption]]
        fun(input)
      }
      else SelectOption(input,input)

      self.setTextboxValue("")
      self.addOption(data)
      self.setCaret(caret)
      self.addItem(data.id)
      //dom.console.log("CREATE works: "+data.toString)

      self.refreshOptions(/*triggerDropdown &&*/ self.settings.mode.asInstanceOf[String] != "single")
      self.unlock()
      true
    } else false

  }

}

class BetterDropdownPlugin(val pluginName:String)
{

  def pluginHandler(self:js.Dynamic,settings:js.Dynamic):Unit =
  {
    val dropDown:ThisFunction0[js.Dynamic,Unit] = positionDropdown _
    self.positionDropdown = dropDown
  }

  def positionDropdown(self:js.Dynamic):Unit = {
    val control = self.$control.asInstanceOf[JQuery]
    val offset = control.position().asInstanceOf[js.Dynamic]
    if(isUndefined(offset.top)) offset.top = 0
    offset.top = offset.top.asInstanceOf[Double] + control.outerHeight(true)
    self.$dropdown.css(js.Dynamic.literal(
      //width = control.outerWidth(),
      top   = offset.top,
      left  = offset.left
    ))

  }

  def pluginFun:ThisFunction1[js.Dynamic,js.Dynamic,Unit] = pluginHandler _

  def activatePlugin() =     SelectizePlugin(pluginName)(pluginFun)

}


/**
 * Binds selecize selects to the property
 * @param view bindable view to which we bind
 * @param model our property model
 * @param suggest suggession handler
 */
class SelectBinder(val view:BindableView, val model:Var[ModelInside], val suggest:(IRI,String)=>Future[Seq[RDFValue]])
  extends ModelBinder(view,model) with BinderWithSelection[PropertySelector]
{
  SelectBinder.activatePlugin()


  protected override def bindRdfInput(el: HTMLElement, key: IRI): Unit =
   {
     this.bindRx(key.stringValue, el: HTMLElement, model) { (e, mod) =>  updateSelector(key, e, mod)  }
   }

  /**
   * Loads data to selector
   * @param key
   * @param e
   * @param mod
   */
  protected def updateSelector(key: IRI, e: HTMLElement, mod: ModelInside): Unit = {
    val sel = this.selectors.getOrElse(e, {
      val s = new PropertySelector(e, key, model)(typeHandler(e, key))
      this.selectors = this.selectors + (e -> s)
      s
    })
    sel.fillValues(mod)
  }


}

trait BinderWithSelection[Selector<:PropertySelector] {

  def suggest:(IRI,String)=>Future[Seq[RDFValue]]

  var selectors: Map[HTMLElement, Selector] = Map.empty[HTMLElement,Selector]

  def typeHandler(el: HTMLElement, key: IRI)(str:String) =
  //this.storage.read()
    this.selectors.get(el) match
    { case Some(sel)=> suggestHandler(sel)(key, str)
    case None=>dom.console.error(s"cannot find selector for ${key.stringValue}")  }

  protected def suggestHandler(sel:Selector)(key: IRI, str: String): Unit = {
    this.suggest(key, str).onComplete {
      case Success(options) =>
        //options.foreach{ case o=> dom.console.info(s"STRING = ${o.stringValue} AND label = ${o.label}") }
        //if(js.isUndefined(sel.sel)) dom.console.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        sel.updateOptions(sel.sel)(options)
      case Failure(thr) => dom.console.error(s"type handler failure for ${key.toString()} with failure ${thr.toString} \n and Stack trace ${thr.stackString}")
    }
  }
}





