package org.denigma.binding.frontend.tests

import org.denigma.binding.views.BindableView
import org.denigma.binding.views.collections.MapCollectionView
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx.{Rx, Var}

import scala.collection.immutable.Map
import scalatags.Text.Tag


/**
 * Class for testing purposes that makes a long list out of test element
 */
class LongListView(element:HTMLElement, params:Map[String,Any]) extends MapCollectionView(element,params){



  val items: Var[List[Map[String, Any]]] = Var{
    List(
      Map("prop"->"value1"),Map("prop"->"value2"),Map("prop"->"value3"),Map("prop"->"value4"),Map("prop"->"value5")
    )

  }

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)

}
