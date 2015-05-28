package controllers.literature

import controllers.endpoints.{ItemsMock, Items}
import org.denigma.schemas.common.BasicSchema
import org.denigma.semweb.rdf.vocabulary._
import org.denigma.semweb.rdf.{BooleanLiteral, IRI, StringLiteral}
import org.denigma.semweb.shex._

object TaskItems extends BasicSchema with ItemsMock{

  lazy val shapeRes = new IRI("http://shape.org")
  val desc = (WI.PLATFORM / "has_description").iri
  val completed = (WI.PLATFORM / "is_completed").iri
  val assigned = (WI.PLATFORM / "assigned_to").iri
  val task = (WI.PLATFORM / "Task").iri
  val Anton = de / "Anton_Kulaga"
  val priority = (WI.PLATFORM / "has_priority").iri

  val Daniel = de / "Daniel_Wuttke"
  val taskShape = new ShapeBuilder(task) has
  de /"is_authored_by" occurs Star and //occurs Plus
  title occurs Star and //occurs ExactlyOne
  //art has de / "date" occurs Star //occurs ExactlyOne
  desc of XSD.StringDatatypeIRI  occurs Star and//occurs Star
  assigned occurs Star and//occurs Plus
  priority occurs Star and //occurs Plus
  completed of XSD.BooleanDatatypeIRI  occurs Star shape//occurs Star


  val taskIntegrase = PropertyModel(
    IRI(WI.PLATFORM /"Integrase"),
    title -> StringLiteral("Find more info"),
    desc->StringLiteral("Find other papers on using PhiC31 integrase for genes insertion") ,
    priority-> de / "high",
    assigned->Anton,
    completed->BooleanLiteral(false) ,
    RDF.TYPE-> task)
  val fungi = PropertyModel(IRI(WI.PLATFORM /"Learn_fungi"),
    title -> StringLiteral("Strong interaction between DAXX and PhiC"),
    desc->StringLiteral("Look at possible interactions") ,
    assigned->Daniel ,
    priority-> de / "low",
    completed->BooleanLiteral(true),
    RDF.TYPE-> task)

 val tasks: List[PropertyModel] = taskIntegrase::fungi::Nil


  def populate(holder:Items)  = {
    holder.items = holder.items + (taskShape.id.asResource->this.tasks)
    holder.shapes = holder.shapes + (taskShape.id.asResource->taskShape)
  }

}