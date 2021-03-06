package org.denigma.preview.slides

import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.{PrefixResolver, RDFModelBinder}
import org.denigma.semantic.models.ModelView
import org.denigma.semantic.{DefaultPrefixes, WebPlatform}
import org.scalajs.dom.raw.Element
import org.w3.banana._
import org.w3.banana.plantain.Plantain
import rx.Var


class RdfSlide(val elem:Element)
  extends BindableView
{

  override lazy val injector = defaultInjector
    .register("TextModelView"){case (el,args)=>new TextModelView(el,None)}

  val modelCode = Var(
    """
      |class TextModelView(elem:Element,resourceOpt:Option[Plantain#URI])(implicit ops:RDFOps[Plantain])
      |  extends ModelView[Plantain](elem,resourceOpt)(ops)
      |{
      |
      |  override lazy val graph: Var[PointedGraph[Plantain]] =  Var(PointedGraph[Plantain](
      |    subject,ops.makeGraph(TestData[Plantain](subject).data))
      |  )
      |
      |  val resolver = new PrefixResolver[Plantain](Var(new DefaultPrefixes[Plantain]().prefixes))
      |
      |  withBinder(me=>new RDFModelBinder[Plantain](graph,  resolver))
      |}
    """.stripMargin)

  val rdfa = Var(
  """
    | <section class="ui blue segment" data-view="TextModelView"
    | vocab="http://webintelligence.eu/platform/"
    | data-param-resource="http://page.org">
    | <p>This model binds to RDF graph</p>
    | <div>
    | <h1 property="title"></h1>
    | <input property="title">
    | <p property="text"></p>
    | <textarea property="text"></textarea>
    |</div>
    |</section>
  """.stripMargin
  )

}

case class TestData[Rdf<:RDF](subject:Rdf#Node)(implicit ops:RDFOps[Rdf]){

  lazy val data = Seq(
    ops.makeTriple(subject,WebPlatform[Rdf](ops)("title"),ops.makeLiteral("Title",ops.xsd.string)),
    ops.makeTriple(subject,WebPlatform[Rdf](ops)("text"),ops.makeLiteral("Some text",ops.xsd.string))
  )

}

class TextModelView(elem:Element,resourceOpt:Option[Plantain#URI])(implicit ops:RDFOps[Plantain])
  extends ModelView[Plantain](elem,resourceOpt)(ops)
{

  override lazy val graph: Var[PointedGraph[Plantain]] =  Var(PointedGraph[Plantain](
    subject,ops.makeGraph(TestData[Plantain](subject).data))
  )

  val resolver = new PrefixResolver[Plantain](Var(new DefaultPrefixes[Plantain]().prefixes))

  withBinder(me=>new RDFModelBinder[Plantain](graph,  resolver))
}