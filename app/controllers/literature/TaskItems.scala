package controllers.literature

import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.rdf.{BooleanLiteral, IRI, StringLiteral}
import org.scalax.semweb.shex.PropertyModel

trait TaskItems extends Items{

  val shapeRes = new IRI("http://shape.org")
  val desc = (WI.PLATFORM / "has_description").iri
  val completed = (WI.PLATFORM / "is_completed").iri
  val assigned = (WI.PLATFORM / "assigned_to").iri
  val task = (WI.PLATFORM / "Task").iri

  val Anton = de / "Anton_Kulaga"
  val Daniel = de / "Daniel_Wuttke"


  val priority = (WI.PLATFORM / "has_priority").iri

  val taskIntegrase = PropertyModel(IRI(WI.PLATFORM /"Integrase"), title -> StringLiteral("Find more info"),  desc->StringLiteral("Find other papers on using PhiC31 integrase for genes insertion") , priority-> de / "high", assigned->Anton, completed->BooleanLiteral(false) , RDF.TYPE-> task)
  val fungi = PropertyModel(IRI(WI.PLATFORM /"Learn_fungi"), title -> StringLiteral("Strong interaction between DAXX and PhiC"),  desc->StringLiteral("Look at possible interactions") , assigned->Daniel , priority-> de / "low", completed->BooleanLiteral(true), RDF.TYPE-> task)

  val tasks = taskIntegrase::fungi::Nil


}