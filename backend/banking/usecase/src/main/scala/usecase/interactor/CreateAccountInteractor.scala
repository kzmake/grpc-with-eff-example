package usecase.interactor

import domain.account.Account
import domain.base.Repository
import domain.eff.Authz
import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import domain.eff.IdGen._idgen
import org.atnos.eff.Eff
import usecase.port.Port
import usecase.port.InputData
import usecase.port.OutputData

class CreateAccountInteractor(
    val accountRepository: Repository[Account]
) extends Port[CreateAccountInputData, CreateAccountOutputData] {
  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  def execute[R: _idgen: _myErrorEither](in: CreateAccountInputData): Eff[R, CreateAccountOutputData] = for {
    account <- Account.applyEff[R]
    created <- accountRepository.add[R](account)
  } yield CreateAccountOutputData(payload = created)
}

case class CreateAccountInputData(principal: String) extends InputData
case class CreateAccountOutputData(payload: Account) extends OutputData
