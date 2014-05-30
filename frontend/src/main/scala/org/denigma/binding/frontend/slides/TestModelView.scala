package org.denigma.binding.frontend.slides

import org.denigma.views.ModelView
import scala.collection.immutable.Map
import rx._
import scalatags._
import rx.core.Var
import org.scalajs.dom.{HTMLElement, MouseEvent}
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf.{RDFValue, StringLiteral, IRI}


/**
 * Test model view
 * @param element
 */
class TestModelView(element:HTMLElement,props:PropertyModel = PropertyModel(properties = Map(IRI("http://hello.world.com")->Set(StringLiteral("HELLO WORLD")),IRI("http://text.com")->Set(StringLiteral("TEXT"))))) extends ModelView("testmodel",element,props){



  val hello = Var("HELLLO VARIABLE")


}
