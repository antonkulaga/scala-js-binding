package controllers.endpoints

import org.scalax.semweb.rdf.{Res, IRI, vocabulary}
import org.scalax.semweb.shex.{PropertyModel, Shape, ShapeBuilder}

trait ItemsMock
{
  var properties = List.empty[IRI]

  def addProperty(iri:IRI): IRI = {
    properties = iri::properties
    iri
  }

  def addProperty(label:String): IRI = this.addProperty(de / label)

  val de = IRI("http://denigma.org/resource/")
  val dc = IRI(vocabulary.DCElements.namespace)

  val rep = new ShapeBuilder(de / "Research_Support")


  val pmid = addProperty(  IRI("http://denigma.org/resource/Pubmed/") )

  val article = addProperty( de /"Article")
  val authors =   addProperty(  de / "is_authored_by")
  val abs =addProperty( de / "abstract")
  val published =addProperty( de / "is_published_in")
  val title = addProperty(de / "title")
  val excerpt = addProperty(de / "excerpt")



}

trait Items {

  var properties = List.empty[IRI]


  var items = Map.empty[Res,List[PropertyModel]]

  var shapes:Map[Res,Shape] = Map.empty

  def addShape(shape:Shape) = {
    val res: Res = shape.id.asResource
    items = items + (res -> List.empty)
    shapes  = shapes + (res->shape)
  }


}
