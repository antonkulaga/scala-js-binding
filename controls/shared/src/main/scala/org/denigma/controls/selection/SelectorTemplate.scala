package org.denigma.controls.selection
import scalatags.Text.all._
object SelectorTemplate {

 /* def template = div( "data-view" := "Selection"

  )*/
"""
  |
  |<section data-view="Selection">
  |  <div class="selection box">
  |    <a data-template="true" class="selection item ui label" data-bind-value="value" data-bind="label" data-style-order="order"></a>
  |    <input class="selection search"  data-bind="input" data-event-keydown="onkeydown" data-style-order="order" autofocus>
  |  </div>
  |  <div class="selection options" data-showif="hasOptions" data-view="options">
  |    <div data-template="true" class="selection option" data-bind-value="value" data-bind="label">Afghanistan</div>
  |  </div>
  |</section>
  |
""".stripMargin



}
