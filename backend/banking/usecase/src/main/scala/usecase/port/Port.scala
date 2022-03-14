package usecase.port

import domain.eff.IdGen._idgen
import org.atnos.eff.Eff

trait InputData
trait OutputData

trait Port[ID <: InputData, OD <: OutputData] {
  def execute[R: _idgen](inputData: ID): Eff[R, OD]
}
