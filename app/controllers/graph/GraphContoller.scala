package controllers.graph


import controllers.PJaxPlatformWith
import org.denigma.binding.messages.GraphMessages
import org.denigma.binding.picklers.rp
import org.denigma.binding.play.{AuthRequest, PickleController, UserAction}
import org.scalajs.spickling.playjson._
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.WI
import play.api.libs.json.Json
import play.api.mvc.Result

import scala.concurrent.Future
import scala.util.Random
import scalax.collection.edge.Implicits._
import scalax.collection.immutable.Graph // shortcuts
/**
 * Graph controller
 */
object GraphController extends PJaxPlatformWith("graph") with PickleController
{
  val rels = List(WI.pl("friend"),WI.pl("enemy"),WI.pl("neutral"))


  def makeNode(i:Int): Res = vocabulary.WI.re(s"node_$i")

  val edges = for{
    a <- 0 until 100
    b <- 0 until  Random.nextInt(10)
    other <- Some(Random.nextInt(100))
    if other!=a

  } yield  (this.makeNode(a) ~+>  this.makeNode(other) ) ( rels(Random.nextInt(rels.size)) )

  lazy val graph = Graph(edges:_*)
  
  type GraphRequest = AuthRequest[GraphMessages.GraphMessage]

  type GraphResult = Future[Result]


  def graphEndpoint = UserAction.async(this.pickle[GraphMessages.GraphMessage]()){implicit request=>
    this.onGraphMessage(request.body)
  }

  def onGraphMessage(message:GraphMessages.GraphMessage)(implicit request:GraphRequest):GraphResult = message match {

    case exp:GraphMessages.NodeExplore=>
      play.Logger.debug(s"${graph.toString()} res = ${exp.resource} ")
      val n: graph.NodeT = this.graph.get(exp.resource)
      val quads = graph.edges.collect{
        case e if e.from==n=>Quad(exp.resource,e.label.asInstanceOf[IRI],e.to.value.asInstanceOf[IRI],IRI(WI.RESOURCE))
        case e if e.to==n=>Quad(e.from.value.asInstanceOf[IRI],e.label.asInstanceOf[IRI],exp.resource,IRI(WI.RESOURCE))
      }
      
      Future.successful(Ok(rp.pickle(quads.toList)).as("application/json"))

    case other=>onBadGraphMessage(message)
  }


  def onBadGraphMessage(message: GraphMessages.GraphMessage)(implicit request: GraphRequest): GraphResult = Future.successful(BadRequest(Json.obj("status" ->"KO","message"->"wrong message type!")).as("application/json"))







}