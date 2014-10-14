package org.denigma.graphs.layouts

import org.scalajs.threejs.Vector3

class LayoutInfo
{

  val pos:Vector3 = new Vector3(0,0,0)
  val offset:Vector3 = new Vector3(0,0,0)


  var force:Double = 0

  def init(v:Vector3) =  {
    if(pos.x==0) pos.x = v.x
    if(pos.y==0) pos.y = v.y
    if(pos.z==0) pos.z = v.z

  }

  def setPoses(x:Double, y:Double, z:Double = 0) ={ pos.x= x; pos.y = y; pos.z = z}
  def setOffsets(x:Double, y:Double, z:Double = 0) ={ offset.x= x; offset.y = y; offset.z = z}

  def clean(){
    setPoses(0,0,0)
    setOffsets(0,0,0)
    force = 0
  }
}
