package org.denigma.binding.semantic
import org.denigma.binding.views.{BindingView, OrganizedView}
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.extensions._
import org.scalax.semweb.rdf._

import scala.collection.immutable.Map

trait RDFView extends OrganizedView
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


  type RDFType = OrganizedView with RDFView

  protected def nearestRDFParent(implicit current:BindingView = this):Option[RDFType] = current.parent match {
    case Some(par:RDFType)=>Some(par)
    case Some(par)=>this.nearestRDFParent(par)
    case _=> None

  }


  protected def bindRdf(el: HTMLElement) = {


    val rp = nearestRDFParent
    prefixes= rp.fold(Map.empty[String,IRI])(_.prefixes)++this.prefixes
    def binded(str:String) = str.contains("data") && str.contains("bind")

    val ats = el.attributes.collect { case (key, value) if !binded(value.value) => (key, value.value.toString)}.toMap

    ats.foreach { case (key, value) =>
      this.rdfPartial(el, key, value,ats).orElse(otherPartial)(key)

    }



  }



  protected def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {

    case "vocab" if value.contains(":") => this.prefixes = prefixes + (":"-> IRI(value))
    //dom.alert("VOCAB=>"+prefixes.toString())

    case "prefix" if value.contains(":")=> this.prefixes = prefixes + (value.substring(0,value.indexOf(":"))-> IRI(value))

  }

  protected def otherPartial: PartialFunction[String, Unit]
}
