package org.denigma.controls

import akka.http.extensions.pjax.{PJaxMagnet, TemplateEngine}
import akka.http.scaladsl.server.Directive

/**
 * PJax Twirl support
 */
class Twirl extends TemplateEngine{
  type Html = play.twirl.api.Html
}

import play.twirl.api.Html

object Twirl{
  implicit def apply(params:(Html,Html=>Html)):PJaxMagnet[Twirl] =
    PJaxMagnet[Twirl](
      Directive[Tuple1[Html]] { inner ⇒ ctx ⇒
        val (html, transform) = params
        if (ctx.request.headers.exists(h => h.lowercaseName() == "x-pjax"))
          inner(Tuple1(html))(ctx)
        else
          inner(Tuple1(transform(html)))(ctx)
    })


}
