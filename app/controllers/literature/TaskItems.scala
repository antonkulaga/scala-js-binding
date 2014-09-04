package controllers.literature

import controllers.endpoints.{ItemsMock, Items}
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.rdf.{BooleanLiteral, IRI, StringLiteral}
import org.scalax.semweb.shex._

object TaskItems extends ItemsMock{

  val shapeRes = new IRI("http://shape.org")
  val desc = (WI.PLATFORM / "has_description").iri
  val completed = (WI.PLATFORM / "is_completed").iri
  val assigned = (WI.PLATFORM / "assigned_to").iri
  val task = (WI.PLATFORM / "Task").iri
  val Anton = de / "Anton_Kulaga"

  val Daniel = de / "Daniel_Wuttke"
  val ts = new ShapeBuilder(task)
  ts has de /"is_authored_by" occurs Star //occurs Plus
  ts has title occurs Star //occurs ExactlyOne
  //art has de / "date" occurs Star //occurs ExactlyOne
  ts has desc of XSD.StringDatatypeIRI  occurs Star//occurs Star
  ts has assigned occurs Star //occurs Plus
  ts has completed of XSD.BooleanDatatypeIRI  occurs Star//occurs Star
  private val taskShape: Shape = ts.result



  val priority = (WI.PLATFORM / "has_priority").iri

  val taskIntegrase = PropertyModel(IRI(WI.PLATFORM /"Integrase"), title -> StringLiteral("Find more info"),  desc->StringLiteral("Find other papers on using PhiC31 integrase for genes insertion") , priority-> de / "high", assigned->Anton, completed->BooleanLiteral(false) , RDF.TYPE-> task)
  val fungi = PropertyModel(IRI(WI.PLATFORM /"Learn_fungi"), title -> StringLiteral("Strong interaction between DAXX and PhiC"),  desc->StringLiteral("Look at possible interactions") , assigned->Daniel , priority-> de / "low", completed->BooleanLiteral(true), RDF.TYPE-> task)

  private val tasks: List[PropertyModel] = taskIntegrase::fungi::Nil


  def populate(holder:Items)  = {
    holder.items = holder.items + (taskShape.id.asResource->this.tasks)
    holder.shapes = holder.shapes + (taskShape.id.asResource->taskShape)
  }

  /**
  val shapeRes = new IRI("http://shape.org")
  val title = (WI.PLATFORM / "title").iri
  val text = (WI.PLATFORM / "text").iri
  val completed = (WI.PLATFORM / "completed").iri
  val task = (WI.PLATFORM / "task").iri


  val writePaper = PropertyModel(IRI(WI.PLATFORM /"WritePaper"), title -> StringLiteral("Write paper"),  text->StringLiteral("I have to write agind as a disease paper") , completed->BooleanLiteral(false) , RDF.TYPE-> task)
  val makeWebsite = PropertyModel(IRI(WI.PLATFORM /"MakeWebsite"), title -> StringLiteral("Make a website"),  text->StringLiteral("I have to make Longevity Ukraine website work") , completed->BooleanLiteral(true), RDF.TYPE-> task)
  val doCRM = PropertyModel(IRI(WI.PLATFORM /"MakeCRM"), title -> StringLiteral("Make ILA CRM"),  text->StringLiteral("I have to make CRM for ILA") , completed->BooleanLiteral(false), RDF.TYPE-> task)

    */


}