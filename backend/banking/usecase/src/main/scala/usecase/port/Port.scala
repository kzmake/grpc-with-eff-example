package usecase.port

import org.atnos.eff.Eff

trait InputData
trait OutputData

trait Port[ID <: InputData, OD <: OutputData] {
  def execute[R](inputData: ID): Eff[R, OD]
}
