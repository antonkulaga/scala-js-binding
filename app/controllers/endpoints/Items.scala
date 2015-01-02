package controllers.endpoints

import org.scalax.semweb.rdf.{Res, IRI, vocabulary}
import org.scalax.semweb.shex.{PropertyModel, Shape, ShapeBuilder}

trait ItemsMock
{
  protected var properties = List.empty[IRI]

  def addProperty(iri:IRI): IRI = {
    properties = iri::properties
    iri
  }

  def addProperty(label:String): IRI = this.addProperty(de / label)

  protected val de = IRI("http://denigma.org/resource/")
  protected val dc = IRI(vocabulary.DCElements.namespace)
  protected val pmid = de / "Pubmed"


  protected val rep = new ShapeBuilder(de / "Research_Support")


  //protected val pmid = addProperty(  IRI("http://denigma.org/resource/Pubmed/") )

  protected val article = addProperty( de /"Article")
  protected val authors =   addProperty(  de / "is_authored_by")
  protected val abs =addProperty( de / "abstract")
  protected val published =addProperty( de / "is_published_in")
  protected val title = addProperty(de / "title")
  protected val excerpt = addProperty(de / "excerpt")


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
