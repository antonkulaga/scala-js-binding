package controllers.endpoints

import org.scalax.semweb.rdf.{Res, IRI, vocabulary}
import org.scalax.semweb.shex.{PropertyModel, Shape, ShapeBuilder}

trait ItemsMock {
  val de = IRI("http://denigma.org/resource/")
  val rep = new ShapeBuilder(de / "Research_Support")


  val pmid = IRI("http://denigma.org/resource/Pubmed/")
  val dc = IRI(vocabulary.DCElements.namespace)

  val article = de /"Article"
  val authors =     de / "is_authored_by"
  val abs = de / "abstract"
  val published = de / "is_published_in"
  val title = de / "title"
  val excerpt = de / "excerpt"
}

trait Items {



  var items = Map.empty[Res,List[PropertyModel]]

  var shapes:Map[Res,Shape] = Map.empty

  def addShape(shape:Shape) = {
    val res: Res = shape.id.asResource
    items = items + (res -> List.empty)
    shapes  = shapes + (res->shape)
  }


}
