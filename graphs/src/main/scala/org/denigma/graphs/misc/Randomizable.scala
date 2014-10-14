package org.denigma.graphs.misc

import org.scalajs.threejs.{Vector3, Vector2}

/**
 * Trait that can generate random values
 */

trait Randomizable
{

  def defRandomDistance:Double = 1000

  def rand(randomDistance:Double = defRandomDistance) = (0.5-Math.random()) * randomDistance
  def rand2() = new Vector2(rand(),rand())
  def rand3() = new Vector3(rand(),rand(),rand())
  def randColor() = Math.random() * 0x1000000

}
