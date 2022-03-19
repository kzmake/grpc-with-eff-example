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

class DeleteAccountInteractor(
    val accountRepository: Repository[Account]
) extends Port[DeleteAccountInputData, DeleteAccountOutputData] {
  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  def execute[R: _idgen: _myErrorEither](in: DeleteAccountInputData): Eff[R, DeleteAccountOutputData] = for {
    account <- accountRepository.resolve[R](Id[Account](in.id))
    _       <- accountRepository.remove[R](account)
  } yield DeleteAccountOutputData()
}

case class DeleteAccountInputData(principal: String, id: String) extends InputData
case class DeleteAccountOutputData()                             extends OutputData
