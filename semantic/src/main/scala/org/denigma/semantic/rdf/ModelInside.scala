package org.denigma.semantic.rdf

import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.PropertyModel

import scala.collection.immutable.Set

object ModelInside
{
  def apply(initial:PropertyModel):ModelInside = {
    require(initial!=null,"initial value for ModelInside cannot be null!")
    ModelInside(initial,initial)
  }

  val empty = ModelInside(PropertyModel.empty)

  def withTerm(mod:PropertyModel,term:IRI): PropertyModel = if(mod.properties.contains(term)) mod else
    mod.copy(properties = mod.properties.updated(term,Set.empty))

}


/**
 * Class tht
 * @param initial model value
 * @param current current model value
 * @param wantsToDie is marked to be deleted (useful for viewmodels)
 */
case class ModelInside(initial:PropertyModel,
                       current:PropertyModel,
                       wantsToDie:Boolean = false,
                        subjectProperties:Set[IRI] = Set.empty[IRI]
                        ) extends ChangeSlot
{

  type Value = PropertyModel



  /**
   * Updates in a list of values
   * @param iri
   * @param value
   * @return
   */
  def add(iri:IRI,value:RDFValue) = this.current.properties.get(iri) match {

    case None=>
      this.replace(iri,value)

    case Some(values) if values.isEmpty=>
      this.replace(iri,value)

    case Some(values)=>

      val changeSub =  value.isInstanceOf[Res] && subjectProperties.contains(iri)
      this.copy(initial,current.copy(  properties = this.current.properties.updated(iri,values+value )))


  }

  def add(iri:IRI,text:String,lang:String): ModelInside  = this.add(iri,StringLangLiteral(text,lang))
  def add(iri:IRI,text:String): ModelInside  = this.add(iri,StringLiteral(text))
  def add(iri:IRI,value:Double): ModelInside  = this.add(iri,DoubleLiteral(value))
  def add(iri:IRI,value:Int): ModelInside  = this.add(iri,IntLiteral(value))



  /**
   * Replaces to one value
   * @param iri
   * @param value
   * @return
   */
  def replace(iri:IRI,value:RDFValue): ModelInside = value match {
    case resValue:Res if subjectProperties.contains(iri) =>
      this.copy(initial,
        current.copy(resource = resValue,properties = current.properties.updated(iri,Set(value))))
    case other=>
      this.copy(initial,current.replace(iri,value))
  }

  def replace(iri:IRI,text:String,lang:String): ModelInside  = this.replace(iri,StringLangLiteral(text,lang))
  def replace(iri:IRI,text:String): ModelInside  = this.replace(iri,StringLiteral(text))
  def replace(iri:IRI,value:Double): ModelInside  = this.replace(iri,DoubleLiteral(value))
  def replace(iri:IRI,value:Int): ModelInside  = this.replace(iri,IntLiteral(value))
  def replace(iri:IRI,values:Set[RDFValue]): ModelInside = this.copy(initial = this.initial,current = current.copy(properties = this.current.properties.updated(iri,values)))

  //def delete(iri:IRI) = this.copy()
  def delete(iri:IRI,value:RDFValue) = this.replace(iri,current.properties.get(iri).map(v=>v - value).getOrElse(Set.empty))
  //def clear(iri:IRI) =  this.replace(iri,current.properties.get(iri).map(v=>v - iri).getOrElse(Set.empty))
  def refresh = this.copy(initial = current)

  def apoptosis: ModelInside = this.copy(wantsToDie = true)
}

trait ChangeSlot {
  self=>

  type Value

  def initial:Value
  def current:Value

  def isUnchanged = initial==current
  def isDirty = initial!=current
  def wantsToDie:Boolean //to mark those models that want to be deleted

}