package org.denigma.semantic.models

import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.ShapeInside
import org.denigma.semweb.rdf.{BlankNode, IRI, Res}
import org.denigma.semweb.shex.{AndRule, IRILabel, Shape}
import rx.core.{Rx, Var}
import rx.ops._


trait WithShapeView  {
  self:BindableView=>

  def shapeOption: Option[Var[ShapeInside]] = this.resolveKeyOption("shape"){
    case sh:Shape=>Var(ShapeInside(sh))
    case sh:ShapeInside=>Var(sh)
    case sh:String if sh.contains("_:") =>Var(ShapeInside(Shape(BlankNode(sh.replace("_:","")), AndRule.empty)))
    case sh:String if sh.contains(":") =>Var(ShapeInside(Shape(IRILabel(IRI(sh)), AndRule.empty)))
    case sh:IRI =>Var(ShapeInside(Shape(IRILabel(sh), AndRule.empty)))
    case sh:Var[ShapeInside] if sh.now.isInstanceOf[ShapeInside]=>sh
    case other=>
      throw new Exception(s"shape param of unsupported ${other.getClass.getName} type in ShapeView $id , shape param value is $other")
  }

  lazy val shapeInside: Var[ShapeInside] =  shapeOption.getOrElse(  Var(ShapeInside(Shape.empty))    )

  lazy val shapeDirty: rx.Rx[Boolean] = shapeInside.map(sh=>sh.isDirty)

  def shapeRes: Rx[Res] = shapeInside.map(sh=>sh.current.id.asResource)
}
