package org.denigma.semantic

import controllers.literature.{ArticleItems, TaskItems}
import org.denigma.binding.messages.ExploreMessages.{Exploration, Explore}
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.Shape
import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification
import prickle.{PConfig, Pickle, Unpickle}

class PicklerTest extends Specification with ShouldMatchers{
  import org.denigma.binding.composites.BindingComposites
  import BindingComposites._
  "pickling" should {

    "work with Explores" in {


      //ExploreMessages.Explore()
      val sh = TaskItems.taskShape
      //Exploration(TaskItems)

      val st = Pickle.intoString[Shape](sh)(BindingComposites.shapePickler,PConfig.Default.copy(areSharedObjectsSupported=false))
      val sto = Unpickle[Shape].fromString(st)
      sto.isSuccess shouldEqual true
      sto.get shouldEqual sh

      val query = IRI("http://ask.me/query?")

      val exp = Explore(query,sh.id.asResource, id = "test")
      val exps = Pickle.intoString[Explore](exp)
      val expo = Unpickle[Explore].fromString(exps)
      expo.isSuccess shouldEqual true
      expo.get shouldEqual exp


      val items = TaskItems.tasks//.toSeq.take(1)

      val exploration = Exploration(sh,items, exp)
      val exples = Pickle.intoString[Exploration](exploration)
      val explo = Unpickle[Exploration].fromString(exples)
      explo.isSuccess shouldEqual true
      explo.get shouldEqual exploration

      true

    }

    "worke with many items" in {
      val sh = ArticleItems.paperShape
      val items = ArticleItems.papers//.toSeq.take(1)

      val query = IRI("http://ask.me/query?")

      val exp = Explore(query,sh.id.asResource, id = "test")

      val exploration = Exploration(sh,items, exp)
      val exples = Pickle.intoString[Exploration](exploration)
      val explo = Unpickle[Exploration].fromString(exples)
      explo.isSuccess shouldEqual true
      explo.get shouldEqual exploration


/*      val items2 = GenesItems.genes
      val exploration2 = Exploration(GenesItems.evidenceShape,items2, exp)
      val exples2 = Pickle.intoString[Exploration](exploration2)
      val explo2 = Unpickle[Exploration].fromString(exples2)
      explo2.isSuccess shouldEqual true
      explo2.get shouldEqual exploration2*/

      true
    }
  }

}
