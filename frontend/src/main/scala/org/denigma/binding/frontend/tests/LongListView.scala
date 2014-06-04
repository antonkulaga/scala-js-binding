package org.denigma.binding.frontend.tests

import org.denigma.views.ListView
import org.scalajs.dom.{Attr, TextEvent, MouseEvent, HTMLElement}
import rx.{Var, Rx}
import scalatags.HtmlTag
import scala.collection.mutable
import scala.collection.immutable.Map


/**
 * Class for testing purposes that makes a long list out of test element
 */
class LongListView(element:HTMLElement, params:Map[String,Any]) extends ListView("lists",element,params){



  val items: Var[List[Map[String, Any]]] = Var{
    List(
      Map("prop"->"value1"),Map("prop"->"value2"),Map("prop"->"value3"),Map("prop"->"value4"),Map("prop"->"value5")
    )

  }



  override lazy val lists: Map[String, Rx[List[Map[String, Any]]]] = this.extractListRx(this)

  override lazy val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  //override lazy val textEvents: Map[String, Var[TextEvent]] = this.extractTextEvents(this)

  override lazy val mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)
}
