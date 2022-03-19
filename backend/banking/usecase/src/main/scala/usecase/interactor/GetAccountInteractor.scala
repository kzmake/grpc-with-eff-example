package usecase.interactor

import domain.account.Account
import domain.base.Repository
import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import domain.eff.IdGen._idgen
import domain.shared.Id
import org.atnos.eff.Eff
import usecase.port.Port
import usecase.port.InputData
import usecase.port.OutputData

class GetAccountInteractor(
    val accountRepository: Repository[Account]
) extends Port[GetAccountInputData, GetAccountOutputData] {
  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  def execute[R: _idgen: _myErrorEither](in: GetAccountInputData): Eff[R, GetAccountOutputData] = for {
    got <- accountRepository.resolve[R](Id[Account](in.id))
  } yield GetAccountOutputData(payload = got)
}

case class GetAccountInputData(principal: String, id: String) extends InputData
case class GetAccountOutputData(payload: Account)             extends OutputData
