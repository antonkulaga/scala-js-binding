package controllers

import org.scalajs.spickling.playjson._
import play.api.mvc.Controller
import org.denigma.binding.models.rp
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf.{StringLiteral, IRI}

object Test extends Controller{


  /**
   * Test html
   * @return
   */
  def html() = UserAction{
    implicit request=>

      val v= views.html.test(request)
      Ok(v)

  }
  def tuple2()= UserAction{
    implicit request=>

      rp.registerPicklers()
      //val value = Seq("one"->"1","two"->"2","three"->"3")
      val value =  ("10","20")
      val pickle = rp.pickle(value)
      Ok(pickle).as("application/json")

  }

  def map()= UserAction{
    implicit request=>

      rp.registerPicklers()
      //val value = Seq("one"->"1","two"->"2","three"->"3")
      val mp =   Map("one"->"first value","two"->"second value","three"->"third value")

      val pickle = rp.pickle(mp)

      Ok(pickle).as("application/json")

  }

  def model()= UserAction{
    implicit request=>

      rp.registerPicklers()
      val value = PropertyModel(properties = Map(IRI("http://hello.world.com")->Set(StringLiteral("HELLO WORLD")),IRI("http://text.com")->Set(StringLiteral("TEXT"))))

      val pickle = rp.pickle(value)

      Ok(pickle).as("application/json")

  }





}
