package usecase.port

import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import domain.eff.IdGen._idgen
import org.atnos.eff.Eff

trait InputData
trait OutputData

trait Port[ID <: InputData, OD <: OutputData] {
  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  def execute[R: _idgen: _myErrorEither](inputData: ID): Eff[R, OD]
}
