package org.denigma.graphs.simple

import org.denigma.graphs.core.VisualGraph
import org.denigma.graphs.misc.Randomizable
import org.denigma.graphs.tools.HtmlSprite
import org.denigma.graphs.visual.{LineParams, Defs, EdgeView, NodeView}
import org.scalajs.dom
import org.scalajs.dom.{Event, MouseEvent, HTMLElement}
import org.scalajs.threejs.Object3D
import org.scalajs.threejs.extensions.Container3D
import org.scalajs.threejs.extensions.controls.JumpCameraControls
import rx.core.Var

class SimpleGraph(val container:HTMLElement,
                   val width:Double = dom.window.innerWidth,
                   val height:Double = dom.window.innerHeight)
  extends VisualGraph with Container3D with Randomizable
   {

     override val controls:JumpCameraControls = new  JumpCameraControls(camera,this.container,scene,this.width,this.height)

     override def defRandomDistance = distance * 0.6

     def randomPos(obj:Object3D) =  obj.position.set(rand(),rand(),rand())

     override type NodeId = String
     override type EdgeId = String
     override type NodeData = Var[String]
     override type EdgeData = Var[String]

     override type ViewOfNode  =  NodeView[Var[String]]
     override type ViewOfEdge = EdgeView[Var[String]]

     type Node = SimpleNode
     type Edge = SimpleEdge

     def onMouseDown(obj:Object3D)( event:MouseEvent ):Unit =  if(event.button==0)
     {
       this.controls.moveTo(obj.position)
     }


     def addNode(id:NodeId,data:NodeData, element:HTMLElement, colorName:String):Node =
       this.addNode(id,data, new ViewOfNode(data,new HtmlSprite(element),colorName))


     override def addNode(id:NodeId,data:NodeData, view:ViewOfNode):Node =
     {
       import view.{sprite => sp}
       this.randomPos(view.sprite)
       val n = new SimpleNode(data,view)
       sp.element.addEventListener( "mousedown", (this.onMouseDown(sp) _).asInstanceOf[Function[Event,_ ]] )
       cssScene.add(view.sprite)
       this.nodes = nodes + (id->n)
       n
     }




     def addEdge(id:EdgeId,from:SimpleNode,to:SimpleNode, data: EdgeData,element:HTMLElement):Edge =
     {
       val color = Defs.colorMap.get(from.view.colorName) match {
         case Some(c)=>c
         case None=>Defs.color
       }
       val sp = new HtmlSprite(element)
       element.addEventListener( "mousedown", (this.onMouseDown(sp) _).asInstanceOf[Function[Event,_ ]] )
       this.controls.moveTo(sp.position)
       //sp.visible = false

       addEdge(id,from,to,data,new EdgeView(from.view.sprite,to.view.sprite,data,sp, LineParams(color)))

     }

     override def addEdge(id:EdgeId,from:SimpleNode,to:SimpleNode, data: EdgeData,view:ViewOfEdge):Edge =
     {
       cssScene.add(view.sprite)
       val e  = new SimpleEdge(from,to,data,view)
       scene.add(view.arrow)
       edges = edges + (id->e)
       e
     }

     override def onEnterFrame() = {
       super.onEnterFrame()
       this.layouts.foreach{case l=>
         if(l.active) l.tick()
         //dom.console.info(s"l is ${l.active}")
       }
     }

   }
