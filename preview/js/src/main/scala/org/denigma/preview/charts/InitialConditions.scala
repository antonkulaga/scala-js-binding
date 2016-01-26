package org.denigma.preview.charts

import rx._
import rx.Ctx.Owner.Unsafe.Unsafe

/**
  * Created by antonkulaga on 11/16/15.
  */
trait InitialConditions {
  lazy val lacI_mRNA_start = Var(0.0)
  lazy val tetR_mRNA_start = Var(0.0)
  lazy val lacI_start = Var(0.0)
  lazy val tetR_start = Var(0.0)
  lazy val initialConditions = Rx{ Array(lacI_mRNA_start(), tetR_mRNA_start(), lacI_start(), tetR_start()) }
}
