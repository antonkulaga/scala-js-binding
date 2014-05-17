package org.denigma.binding.frontend

import org.denigma.views.OrdinaryView
import org.scalajs.dom.{MouseEvent, HTMLElement}
import rx.{Rx, Var}
import scalatags.HtmlTag

/**
 * View for article with some text
 */
class ArticleView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("article",element){
  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)


  val authors = Var("Илья Стамблер, Дарья Халтурина")
  val title = Var("Манифест Международного Альянса за Продление Жизни")

  val t = Var("""
    <p>We advocate the advancement of healthy longevity for the entire population through scientific research, public health, advocacy and social activism. We emphasize and promote the struggle against the chief enemy of healthy longevity – the aging process.</p>
    <p>The aging process is the root of most chronic diseases afflicting the world population. This process causes the largest proportion of disability and mortality, and needs to be treated accordingly. Society needs to dedicate efforts toward its treatment and correction, as for any other material disease.</p>
    <p>The problem of aging is grave and threatening. Yet, we often witness an almost complete oblivion to its reality and severity. There is a soothing tendency to ignore the future, to distract the mind from aging and death from aging, and even to present aging and death in a misleading, apologetic and utopian light. At the same time, there is an unfounded belief that aging is a completely unmanageable, inexorable process. This disregard of the problem and this unfounded sense of impotence do not contribute to the improvement of the well-being of the aged and their healthy longevity. There is a need to present the problem in its full severity and importance and to act for its solution or mitigation to the best of our ability.</p>
    <p>We call to raise the public awareness of the problem of aging in its full scope. We call the public to recognize this severe problem and dedicate efforts and resources – including economic, social-political, scientific, technological and media resources – to its maximal possible alleviation for the benefit of the aging population, for their healthy longevity. We promote the idea that mental and spiritual maturation and the increase in healthy longevity are not synonymous with aging and deterioration.</p>
    <p>We advocate the reinforcement and acceleration of basic and applied biomedical research, as well as the development of technological, industrial, environmental, public health and educational measures, specifically directed for healthy longevity. If given sufficient support, such measures can increase the healthy life expectancy of the aged population, the period of their productivity, their contribution to the development of society and economy, as well as their sense of enjoyment, purpose and valuation of life.</p>
    <p>We advocate that the development of scientific measures for healthy life extension be given the maximal possible public and political support that it deserves, not only by the professional community but also by the broad public.</p>
    """
    )

  val text = Var(
    """
      | <p> Мы выступаем за достижение населением здорового и активного долголетия, которое должно стать результатом широких научных исследований, развития здравоохранения, просветительной и общественной деятельности. Мы призываем к борьбе с основным врагом активного долголетия – деструктивным процессом старения. Процесс старения является основной причиной большинства хронических заболеваний, поражающих людей во всем мире, приводит к инвалидности и смерти, и необходимо относиться к нему соответственно. Общество должно направить усилия на его коррекцию, на лечение старения, как в случае любой физической болезни.</p>
      | <p>Старение – серьезная и опасная проблема. Однако мы зачастую являемся свидетелями почти полного забвения ее реальности и опасности. Налицо успокоительная тенденция игнорировать будущее, отвлекать наше сознание от реальности старения и смерти и даже представлять смерть в обманчивом, примиренческом и утопическом свете. В то же время существует необоснованное убеждение, что старение – абсолютно неуправляемый, неизбежный процесс. Это игнорирование проблемы в сочетании с необоснованным чувством бессилия тормозит нашу победу над старением, не способствуют улучшению качества жизни людей старшего поколения, их активному долголетию. Необходимо представить проблему старения во всей ее серьезности и важности и действовать для ее полного или хотя бы частичного решения, насколько позволяют наши возможности.</p>
      | <p>Мы призываем пробудить в общественном сознании внимание к проблеме старения в ее полном объеме. Мы призываем общество признать тяжесть этой проблемы, сконцентрировать вокруг нее человеческие усилия и направить все возможные ресурсы – в том числе экономические, социально-политические, научные, технологические, информационные – на ее максимальное облегчение, на улучшение благополучия стареющего населения, его здорового долголетия. Мы отстаиваем идею, что духовная и интеллектуальная зрелость и увеличение активной продолжительности жизни не являются синонимами старческой дегенерации и разрушения.</p>
      | <p>Мы призываем содействовать расширению и ускорению фундаментальных и прикладных биомедицинских исследований, а также разработке технологических, экономических, экологических, здравоохранительных и образовательных мер, направленных на увеличение здорового долголетия. Эти меры при условии их необходимой поддержки способны увеличить активную продолжительность жизни стареющего населения, продлить период продуктивной деятельности людей пожилого возраста, их вклад в развитие общества и экономики, а также их чувство достоинства, радости и ценности жизни.</p>
      | <p>Мы выступаем за то, чтобы развитию научных средств, направленных на достижение активного долголетия, была предоставлена максимальная общественная и политическая поддержка. Активное долголетие как цель заслуживает именно такой поддержки – не только в профессиональной среде, но и во всем обществе.</p>
    """.stripMargin)

  val published = Var("01/01/2013")
  val lastEdited = Var("01/01/2014")



}
