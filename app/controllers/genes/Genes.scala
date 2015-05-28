package controllers.genes

import controllers._
import org.denigma.endpoints.{UserAction, UserRequestHeader}
import org.denigma.schemas.genes.LoadGenAge
import org.denigma.semweb.rdf.{BasicTriplet, IRI, Quad, Trip}
import org.denigma.semweb.shex.PropertyModel
import org.openrdf.model.Statement
import play.api.mvc.{RequestHeader, Result}
import play.twirl.api.Html

import scala.io.Source

/**
 * Literature controller
 */
object Genes extends PjaxController with LoadGenAge{

  override def page(implicit request:UserRequestHeader,html:Option[Html] = None,into:String = "main"): Result = {
    Ok(views.html.index(request,html,into,false))
  }


  def reports() = UserAction{implicit request=>
    this.pj(views.html.genes.datagrid()(request))
  }


  def testGenes() = UserAction {
    implicit request =>
      //val fileName = "resources/data_from_geneage.csv"
      val fileName = "resources/annotations.tsv"
      val str = readFrom(fileName)(request)

      val indexed: List[PropertyModel] = testGenesTable(str)
      val trips = (for{
        mod <- indexed
        id = mod.id
        (prop,values) <- mod.properties
        obj <-values
      } yield Trip(id,prop,obj)).toSet

      Ok(this.writeTurtle(trips,this.prefixes))

    //    Ok(trips.toString)
  }

  /**
   * Writes turtle to string
   * @param trips
   * @param prefs
   * @tparam T
   * @return
   */
  def writeTurtle[T<:BasicTriplet](trips:Set[T],prefs:Seq[(String,String)]): String =  TurtleMaster.write(trips,prefs:_*).get



  def ontology() = UserAction {
    implicit request=>
      import org.denigma.semweb.sesame._

      import scala.collection.JavaConversions._

      val statements: List[Statement] = Ontology.allFacts.flatMap(g=>g.graph)

      val sts = statements.map(st=>Trip(st.getSubject,st.getPredicate,st.getObject))
      val facts:Set[Trip] = Set(sts:_*)

        //.map(st:Statement=>Trip(st.getSubject,st.getPredicate,st.getObject))
      Ok(this.writeTurtle[Trip](facts,this.prefixes))

  }


  def testSchemaWriting() = UserAction {
    val quads: Set[Quad] = this.evidenceShape.asQuads(IRI("http://denigma.org/resource/"))
    val str = this.writeTurtle(quads,this.prefixes)//TurtleMaster.simpleWrite(quads)
    Ok(str)
  }


  def readFrom(path:String)(implicit request:RequestHeader): String = {
    val url: String = controllers.routes.Assets.at(path).absoluteURL(secure = false)(request)
    Source.fromURL(url).getLines().reduce(_+"\n"+_)
  }



}
