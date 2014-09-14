package org.denigma.semantic.binders

import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.ModelInside
import org.denigma.semantic.binders.selectors.PropertySelector
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.{IRI, RDFValue}
import rx.core.Var

import scala.collection.immutable.Map
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

trait GeneralSelectBinder
{
  type Element
  type View<:BindableView
  type Selector

  val view:View
  val model:Var[Element]
  var selectors = Map.empty[HTMLElement,Selector]
}

/**
 * Binds selecize selects to the property
 * @param view
 * @param model
 * @param suggest
 */
class SelectBinder(val view:BindableView, val model:Var[ModelInside], suggest:(IRI,String)=>Future[List[RDFValue]])
  extends ModelBinder(view,model)
{
  type Selector = PropertySelector
  var selectors = Map.empty[HTMLElement,Selector]


   protected override def bindRdfInput(el: HTMLElement, key: IRI): Unit =
   {

     this.bindRx(key.stringValue, el: HTMLElement, model) { (e, mod) =>
          val sel = this.selectors.getOrElse(e, {
            val s = new PropertySelector(e, key, model, typeHandler(e, key))
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
       case Some(s)=>
         this.suggest(key,str).onComplete{
           case Success(options)=>s.updateOptions(options)
           case Failure(th)=>dom.console.error(s"type handler failure for ${key.toString()} with failure ${th.toString}")
         }
       case None=>dom.console.error(s"cannot find selector for ${key.stringValue}")
       //dom.console.log("typed = "+str)
     }

 }
