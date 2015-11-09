package org.denigma.preview.charts

/**
  * Created by antonkulaga on 11/9/15.
  */
object Tester extends scala.App
{
  val ode = new CompBioODEs()
  val lp = ode.lacRepressedByTetR
  val tp = ode.tetRepressedByLacI
  val lprod = ode.lacIProduction
  val tprod = ode.tetRProduction

  //println(tprod(2.6007 , 816.6442  )) // -1.1132

  //println(tp.repress(1.7736,  915.4316  )) //    0.0141
  //println(tp.repress(2.6007 , 778.9885)) //  0.0115

  /*

    println(lp.repress(17.9364  , 402.2302   )) //-0.0044

    println(lp.repress(17.9134  , 405.6567   )) //-0.0106

    println(lp.repress(17.8766  , 409.0670   )) //-0.0163

    println(lp.repress(17.8766  , 409.0670   )) //-0.0163

    println(lp.repress(17.8766  , 409.0670   )) //-0.0163

    println(lp.repress(17.8766  , 409.0670   )) //-0.0163
  */


}
