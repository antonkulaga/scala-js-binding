package org.denigma.preview.charts

import org.denigma.controls.charts.ode.{ODEs, VectorODESolver}

case class Reactant(concentration:Double)

case class HillRepression(kProd: Double, kRepress: Double, nRepressor: Double, delusion: Double, leakage: Double) {

  def repress(mRNA:Double,repressor: Double): Double = {
    //if (value.isNaN) throw new Exception(s"IS NAN: with product $product and repressor $repressor and dis Repressor $k_disRepressor")
    //if (value.isInfinite) throw new Exception(s"IS INFINIT: with product $product and repressor $repressor and dis Repressor $k_disRepressor")
    val result = kProd / (1 + Math.pow( (repressor / kRepress), nRepressor) ) - delusion * mRNA + leakage
    result
  }
}

case class ProductionDelusion(production: Double, delution: Double){

  def apply(x: Double, y: Double): Double = production*x - y*delution
  def apply(x: Double): Double = apply(x, x)

}

object Defaults1 {


  lazy val gamma_L_m: Double = 0.04
  lazy val gamma_T_m: Double = 0.04
  lazy val kappa_L_m0: Double = 0.0082
  lazy val kappa_T_m0: Double = 0.0149
  lazy val kappa_L_m: Double = 1
  lazy val kappa_T_m: Double = 0.3865

  lazy val gamma_L_p: Double = 0.002
  lazy val gamma_T_p: Double = 0.002
  lazy val kappa_L_p: Double = 0.1
  lazy val kappa_T_p: Double = 0.2
  lazy val theta_L: Double = 600.0
  lazy val theta_T: Double = 500.0
  lazy val eta_L: Double = 4
  lazy val eta_T: Double = 4

  lazy val lacIRepression = HillRepression(kappa_L_m, theta_T, eta_T,gamma_L_m, kappa_L_m0)
  lazy val tetRRepression = HillRepression(kappa_T_m, theta_L, eta_L,gamma_T_m, kappa_T_m0)
  lazy val lacIProduction = ProductionDelusion(kappa_L_p, gamma_L_p)
  lazy val tetRProduction = ProductionDelusion(kappa_T_p, gamma_T_p)
}

object Defaults {

  lazy val gamma_L_m: Double = Math.log(2)/3
  lazy val gamma_T_m: Double = Math.log(2)/3
  lazy val kappa_L_m0: Double = 0.0
  lazy val kappa_T_m0: Double = 0.0
  lazy val kappa_L_m: Double = 20 * gamma_L_m
  lazy val kappa_T_m: Double = 20 * gamma_T_m
  lazy val gamma_L_p: Double = Math.log(2)/20
  lazy val gamma_T_p: Double = Math.log(2)/20
  lazy val kappa_L_p: Double = 2000 * gamma_L_p / 20
  lazy val kappa_T_p: Double = 2000 * gamma_T_p / 20
  lazy val theta_L: Double = 1000.0
  lazy val theta_T: Double = 1000.0
  lazy val eta_L: Double = 2.5
  lazy val eta_T: Double = 2.5

  lazy val lacIRepression = HillRepression(kappa_L_m, kappa_T_m, eta_T, gamma_L_m, kappa_L_m0)
  lazy val tetRRepression = HillRepression(kappa_T_m, kappa_L_m, eta_L, gamma_T_m, kappa_T_m0)
  lazy val lacIProduction = ProductionDelusion(kappa_L_p, gamma_L_m)
  lazy val tetRProduction = ProductionDelusion(kappa_T_p, gamma_T_m)

}

case class CompBioODEs(
                        lacRepressedByTetR: HillRepression = Defaults1.lacIRepression,
                        tetRepressedByLacI: HillRepression = Defaults1.tetRRepression,
                        lacIProduction: ProductionDelusion = Defaults1.lacIProduction,
                        tetRProduction: ProductionDelusion = Defaults1.tetRProduction,
                        tEnd: Double = 5000, override val step: Double = 1) extends ODEs
{

  override val tStart = 0.0

  def d_LacI_mRNA (t: Double, p: Array[Double]): Double =  lacRepressedByTetR.repress(p(0), p(3))
  def d_TetR_mRNA (t: Double, p: Array[Double]): Double = tetRepressedByLacI.repress(p(1), p(2))
  def d_LacI (t: Double, p: Array[Double]): Double = lacIProduction(p(0), p(2))
  def d_TetR (t: Double, p: Array[Double]): Double = tetRProduction(p(1), p(3))

  lazy val derivatives: Array[VectorDerivative] = Array(d_LacI_mRNA, d_TetR_mRNA, d_LacI, d_TetR)
  import VectorODESolver._

  def solve(lacI_mRNA: Double, tetR_mRNA: Double, lacI: Double, tetR: Double) = {
    val result = this.compute(Array(lacI_mRNA: Double, tetR_mRNA: Double, lacI: Double, tetR: Double))
    result
  }

}