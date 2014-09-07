package org.denigma.semantic.rdf

import org.denigma.binding.binders.BasicBinding
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf._

import scala.collection.immutable.Map

/**
 * View that can do RDFa binding (binding to semantic properties)
 */
class RDFBinder(view:BindableView) extends BasicBinding
{


  /**
   * Default vocabulary is ""
   */
  var prefixes = Map.empty[String,IRI]


  implicit val context = IRI("http://"+dom.location.hostname)

  /**
   * Resolvers IRI from property map
   * @param property
   * @return
   */
  def resolve(property:String):Option[IRI] =  property.indexOf(":") match {
    case -1 =>
      //dom.alert(prefixes.toString())
      prefixes.get(":").orElse(prefixes.get("")).map(p=>p / property)
    case 0 =>
      //dom.alert("0"+prefixes.toString())
      prefixes.get(":").orElse(prefixes.get("")).map(p=>p / property)
    case ind=>
      val key = property.substring(0,ind)

      prefixes.get(key).map(p=>p / property).orElse(Some(IRI(property)))

  }

  def nearestRDFBinders(): List[RDFBinder] = view.nearestParentOf{
    case p:BindableView if p.binders.exists(b=>b.isInstanceOf[RDFBinder])=>p.binders.collect{case b:RDFBinder=>b}
  }.getOrElse(List.empty)


  protected def updatePrefixes(el:HTMLElement) = {
    val rps: List[RDFBinder] = this.nearestRDFBinders()
    prefixes = rps.foldLeft(Map.empty[String,IRI])((acc,el)=>acc++el.prefixes)++this.prefixes
  }

  protected def forBinding(str:String) = str.contains("data") && str.contains("bind")


  /**
   * Returns partial function that extracts vocabulary data (like prefixes) from html
   * @param value
   * @return
   */
  protected def vocabPartial(value: String): PartialFunction[String, Unit] ={

    case "vocab" if value.contains(":") => this.prefixes = prefixes + (":"-> IRI(value))
    //dom.alert("VOCAB=>"+prefixes.toString())

    case "prefix" if value.contains(":")=> this.prefixes = prefixes + (value.substring(0,value.indexOf(":"))-> IRI(value))
  }


  /**
   * Binds vocabs and prefixes
   * @param el html element to bind to
   * @param key Key
   * @param value Value
   * @param ats attributes
   * @return
   */
  protected def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] =  this.vocabPartial(value)



  override def bindAttributes(el: HTMLElement, ats: Map[String, String]): Unit = {

    this.updatePrefixes(el)

    ats.foreach { case (key, value) =>
      this.rdfPartial(el, key, value,ats).orElse(otherPartial)(key)
    }

  }

  override def id: String = view.id
}
