package org.denigma.binding.frontend

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.views.BindableView
import org.denigma.graphs.GraphView
import org.scalajs.dom
import org.scalajs.dom.{Event, XMLHttpRequest, HTMLElement}
import org.scalax.semweb.rdf.IRI
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.{Dynamic, JSON}
import org.denigma.binding.extensions._

class Graph2Slide(elem:HTMLElement, params:Map[String,Any]) extends GraphView(elem:HTMLElement,params:Map[String,Any])
{

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  val apply = Var(EventBinding.createMouseEvent())

  override protected def attachBinders(): Unit = binders = Nil



  override def bindView(el:HTMLElement) = {
    super.bindView(el)

  }


}

class GraphSlide(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView
{


  lazy val path: String = this.params.get("path").map(_.toString).get

  lazy val resource = this.params.get("resource").map(v=>IRI(v.toString)).get

  //require(params.contains("path"))


  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}




  override def bindView(el:HTMLElement) = {
    super.bindView(el)
    this.draw()
    //jQuery(el).slideUp()
    //    super.bindView(el)
    //    Sigma.utils.pkg("sigma.canvas.edges")
    //    this.sigma =  new Sigma(initial)
    //    this.storage.explore(this.resource).onComplete{
    //      case Success(data) =>
    //        this.loadData(data)
    //      case Failure(th)=>
    //        dom.console.error(s"failure in read of model for $path: \n ${th.getMessage} ")
    //    }

  }


  protected def draw() = {

  }


  //  override protected def loadData(data:List[Quad]) = {
  //    super.loadData(data)
  //    sigma.startForceAtlas2()
  //  }

  def container: HTMLElement = dom.document.getElementById("graph-container")
  override protected def attachBinders(): Unit = binders =  BindableView.defaultBinders(this)
}



class GlobeSlide(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView
{


  lazy val path: String = this.params.get("path").map(_.toString).get

  lazy val resource = this.params.get("resource").map(v=>IRI(v.toString)).get


  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


  override def bindView(el:HTMLElement) = {
    super.bindView(el)
    js.eval(
      """
      |        var years = ['1990','1995','2000'];
      |        var container = document.getElementById('container');
      |        var globe = new DAT.Globe(container);
      |
      |        console.log(globe);
      |        var i, tweens = [];
      |
      |        var settime = function(globe, t) {
      |        return function() {
      |        new TWEEN.Tween(globe).to({time: t/years.length},500).easing(TWEEN.Easing.Cubic.EaseOut).start();
      |        var y = document.getElementById('year'+years[t]);
      |        if (y.getAttribute('class') === 'year active') {
      |        return;
      |        }
      |        var yy = document.getElementsByClassName('year');
      |        for(i=0; i<yy.length; i++) {
      |        yy[i].setAttribute('class','year');
      |        }
      |        y.setAttribute('class', 'year active');
      |        };
      |        };
      |
      |        for(var i = 0; i<years.length; i++) {
      |        var y = document.getElementById('year'+years[i]);
      |        y.addEventListener('mouseover', settime(globe,i), false);
      |        }
      |
      |        TWEEN.start();
      |        var xhr;
      |        xhr = new XMLHttpRequest();
      |        xhr.open('GET', '/public/globe/population909500.json', true);
      |        xhr.onreadystatechange = function(e) {
      |        if (xhr.readyState === 4) {
      |        if (xhr.status === 200) {
      |        var data = JSON.parse(xhr.responseText);
      |        window.data = data;
      |        for (i=0;i<data.length;i++) {
      |        globe.addData(data[i][1], {format: 'magnitude', name: data[i][0], animated: true});
      |        }
      |        globe.createPoints();
      |        settime(globe,0)();
      |        globe.animate();
      |        document.body.style.backgroundImage = 'none'; // remove loading
      |        }
      |        }
      |        };
      |        xhr.send(null);

    """.
        stripMargin
    )
//    val xhr = new   XMLHttpRequest()
//
//    xhr.open("GET", "/public/globe/population909500.json", true)
//
//    def loadGlobe(event:Event) = {
//      var data = JSON.parse(xhr.responseText)
//      if (xhr.readyState == 4) {
//        if (xhr.status == 200) {
//          var data: Dynamic = JSON.parse(xhr.responseText)
//          dom.window.dyn.data = data
//          debug(data.toString)
//        }
//      }
//    }
//    xhr.onreadystatechange = loadGlobe _
//    xhr.send(null)
  }


override protected def attachBinders(): Unit = binders =  BindableView.defaultBinders(this)


  """
    |        var xhr;
    |        xhr = new XMLHttpRequest();
    |        xhr.open('GET', '/public/globe/population909500.json', true);
    |        xhr.onreadystatechange = function(e) {
    |        if (xhr.readyState === 4) {
    |        if (xhr.status === 200) {
    |        var data = JSON.parse(xhr.responseText);
    |        window.data = data;
    |        for (i=0;i<data.length;i++) {
    |        globe.addData(data[i][1], {format: 'magnitude', name: data[i][0], animated: true});
    |        }
    |        globe.createPoints();
    |        settime(globe,0)();
    |        globe.animate();
    |        document.body.style.backgroundImage = 'none'; // remove loading
    |        }
    |        }
    |        };
    |        xhr.send(null);
  """.stripMargin

}
