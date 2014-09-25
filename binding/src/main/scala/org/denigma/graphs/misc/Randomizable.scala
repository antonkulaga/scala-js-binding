package org.denigma.graphs.misc

import org.scalajs.threejs.{Vector3, Vector2}

/**
 * Trait that can generate random values
 */
trait Randomizable
{

  def randomDistance:Double
  def rand() = (0.5-Math.random()) * randomDistance
  def rand2() = new Vector2(rand(),rand())
  def rand3() = new Vector3(rand(),rand(),rand())
  def randColor() = Math.random() * 0x1000000

}
