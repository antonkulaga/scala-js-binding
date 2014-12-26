package org.denigma.semantic.binders

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.{KeyboardEvent, Event, HTMLElement}
import org.scalajs.jquery._
import org.scalajs.selectize.{SelectizePlugin, Selectize}
import org.scalax.semweb.rdf.{IRI, RDFValue}
import rx.Var

import scala.collection.immutable.{Seq, Map}
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.{Function1, ThisFunction0, ThisFunction1}
import scala.scalajs.js.annotation.JSExportAll
import scala.util.{Failure, Success}
import scalatags.Text.all._

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

  def createItem(self:Selectize)(triggerDropdown:Any):Boolean =  {
    val input = if(self.$control_input==null) "" else self.$control_input.`val`().toString
    if(self.canCreate(input)){
      val caret = self.caretPos
      self.lock()
      val data:SelectOption =  if(self.settings.createItem!=null)
        self.settings.createItem.asInstanceOf[js.Function1[String,SelectOption]](input)
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

class BetterDropdownPlugin(val pluginName:String) {

  def pluginHandler(self:js.Dynamic,settings:js.Dynamic):Unit =
  {
    val dropDown:ThisFunction0[js.Dynamic,Unit] = positionDropdown _
    self.positionDropdown = dropDown
  }


  def positionDropdown(self:js.Dynamic):Unit = {
    //dom.console.log("dropdown works!")
    val control = self.$control
    val offset = control.position
    offset.top = offset.top + control.outerHeight(true).asInstanceOf[Double]
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
class SelectBinder(val view:BindableView, val model:Var[ModelInside], suggest:(IRI,String)=>Future[List[RDFValue]])
  extends ModelBinder(view,model)
{

  SelectBinder.activatePlugin()


  type Selector = PropertySelector
  var selectors = Map.empty[HTMLElement,Selector]


  protected override def bindRdfInput(el: HTMLElement, key: IRI): Unit =
   {

     this.bindRx(key.stringValue, el: HTMLElement, model) { (e, mod) =>
          val sel = this.selectors.getOrElse(e, {
            val s = new PropertySelector(e, key, model)( typeHandler(e, key) )
            this.selectors = this.selectors + (e -> s)
            s
          })

       sel.fillValues(mod)
     }
   }

   def typeHandler(el: HTMLElement, key: IRI)(str:String) =
   //this.storage.read()
     this.selectors.get(el) match
     {
       case Some(sel)=>
         this.suggest(key,str).onComplete{
           case Success(options)=>
             //options.foreach{ case o=> dom.console.info(s"STRING = ${o.stringValue} AND label = ${o.label}") }
             sel.updateOptions(options)
           case Failure(thr)=>dom.console.error(s"type handler failure for ${key.toString()} with failure ${thr.toString}")
         }
       case None=>dom.console.error(s"cannot find selector for ${key.stringValue}")
     }

 }





