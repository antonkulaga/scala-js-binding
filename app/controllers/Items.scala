package controllers

import org.scalax.semweb.rdf.{IRI, vocabulary}
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



  var items:Map[String,List[PropertyModel]] = Map.empty

  var shapes:Map[String,Shape] = Map.empty

  /**
   * function that extracts items from item collection in channel name contains itemlist key
   * TODO: change this to something more reliable and clear
   * @param channel
   * @return
   */
  def extract(channel:String) = items.collectFirst{case (key,value) if channel.contains(key)=>value}

  def extractShape(channel:String) = shapes.collectFirst{case (key,value) if channel.contains(key)=>value}

  def extractKV(channel:String): Option[(String, List[PropertyModel])] = items.keys.collectFirst{case key if channel.contains(key)=>key->items(key)}

}
