package org.denigma.preview.slides

import org.denigma.binding.binders.{ReactiveBinder, GeneralBinder}
import org.denigma.binding.views.BindableView
import org.denigma.controls.charts._
import org.scalajs.dom._
import rx._
import rx.core.Var
import rx.ops._
import scala.annotation.tailrec
import scala.collection.immutable._

import scala.collection.immutable.Seq

class CompBioView(val elem:Element) extends BindableView with Series
{

  val scaleX = Var(LinearScale(0,1000,1000))
  val scaleY = Var(LinearScale(0,1000,1000,inverted = true))

  val triangle = Var(List(  Point(100,100),Point(200,200),Point(300,100)  ) )

  //  <path d="M150 0 L75 200 L225 200 Z" />

  override lazy val injector = defaultInjector
    .register("chart"){
      case (el,params)=>
        new ChartView(el).withBinder(new GeneralBinder(_,this.binders.collectFirst{case r:ReactiveBinder=>r}))
    }
    .register("triangle"){
      case (el,params)=> new PathView(el,triangle).withBinder(new GeneralBinder(_))
    }
}

class PathView(val elem:Element, val points:Rx[List[Point]],closed:Boolean = true) extends BindableView {

  val movements = points.map(points2ToMovements(_))

  val path = movements.map(movements2Path)

  println(
    s"""
      |points are ${points.now.mkString(" | ")}
      |movements are ${movements.now.mkString(" | ")}
      |path is ${path}
    """.stripMargin)


  @tailrec final def points2ToMovements(items:List[Point],acc:List[Point] = List.empty):List[Point]  = items match {
    case head::tail=>
      val newAcc = if(acc.isEmpty) head::Nil else head.copy(head.x-acc.head.x,head.y-acc.head.y)::acc
      points2ToMovements(tail,newAcc)
    case Nil=> acc.reverse
  }

  def movements2Path(items:List[Point]): String = items match {
    case Point(sx,sy)::tail=> tail.foldLeft(s"M$sx $sy") {
        case (acc, Point(x,y))=>acc+s" L$x $y"
      } + (if(closed)" Z" else "")
    case Nil=> if(closed)" Z" else ""
  }


 /*
  M = moveto
  L = lineto
  H = horizontal lineto
  V = vertical lineto
  C = curveto
  S = smooth curveto
  Q = quadratic Bézier curve
  z = smooth quadratic Bézier curveto
  A = elliptical Arc
  Z = closepath*/
}

class ChartView(elem:Element) extends ScatterPlot(elem,
  Var(LinearScale(0,1000,100)),
  Var(LinearScale(0,1000,100,inverted = true)))
{

  override val items: Rx[Seq[Var[PointValue]]] = Var(Seq(
    Var(PointValue(0,0,"Hello")),
    Var(PointValue(100,100,"World")),
    Var(PointValue(200,100,"Winter")),
    Var(PointValue(100,200,"Is coming"))
  ))

}
