package org.denigma.semantic.binders

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.jquery._
import org.scalajs.selectize.{SelectizePlugin, Selectize}
import org.scalax.semweb.rdf.{IRI, RDFValue}
import rx.Var

import scala.collection.immutable.Map
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.{ThisFunction0, ThisFunction1}
import scala.scalajs.js.annotation.JSExportAll
import scala.util.{Failure, Success}
import scalatags.Text.all._

object SelectBinder {

  val pluginName:String = "select_binder"

  def pluginHandler(self:js.Dynamic,settings:js.Dynamic):Unit =
  {
    val dropDown:ThisFunction0[js.Dynamic,Unit] = positionDropdown _
    self.positionDropdown = dropDown
  }


  def positionDropdown(self:js.Dynamic):Unit = {
    dom.console.log("dropdown works!")
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
/**
 * Selects property from the model
 * @param el html element of the view from which there will be selection
 * @param key iri of the property
 * @param model property model with props and vals
 * @param typeHandler handler that react on typing
 */
class PropertySelector(val el:HTMLElement,val key:IRI,val model:Var[ModelInside])(typeHandler:(String)=>Unit) extends SemanticSelector
{

  val sel: js.Dynamic = jQuery(el).dyn.selectize(selectParams(el))



  protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      delimiter = "|",
      persist = false,
      valueField = "id",
      labelField = "title",
      searchField = "title",
      onType = typeHandler  ,
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      options = makeOptions(),
      render = semanticRenderer,
      copyClassesToDropdown = false,
      plugins = js.Array(SelectBinder.pluginName)
    )
  }

  def makeOptions() =
    this.model.now.current.properties.get(key) match {
      case Some(iris)=>
        val o= iris.map(i=> makeOption(i)).toList
        js.Array( o:_* )
      case None=> js.Array()
    }


  protected def itemAddHandler(text:String, item:js.Dynamic): Unit = {
    val value = unescape(text)
    //dom.console.log("ADDED = " + item+ " WITH VALUE = "+value)

    val mod = model.now
    mod.current.properties.get(key) match {
      case None=> model() = mod.add(key,parseRDF(value))
      case Some(ps) => if(!ps.exists(p=>p.stringValue==value)) model() = mod.add(key,parseRDF(value))
    }
  }

  protected def itemRemoveHandler(value:String): Unit = {
    val mod =  model.now
    val remove: Option[Set[RDFValue]] =mod.current.properties.get(key).map{ps=>  ps.collect{case p if p.stringValue==value=>p}    }
    if(remove.nonEmpty) {
      val n = this.parseRDF(value)
      val s1 = mod.current.properties(key).size
      val md = mod.delete(key,n)
      val s2 = md.current.properties(key).size
      //dom.console.log(s"s1 = $s1 | s2 = $s2")
      model() = md
    }
  }


  /**
   * Fills values from a property model
   * @param model
   * @return
   */
  def fillValues(model: ModelInside):this.type = {
    val ss= this.selectizeFrom(el)
    ss.clearOptions()
    model.current.properties.get(key).foreach{ps=>
      ps.foreach{p=>
        ss.addOption(this.makeOption(p))
        ss.addItem(this.escape(p.stringValue))
      }
    }
    this
  }

}
