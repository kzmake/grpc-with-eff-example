package usecase.interactor

import domain.account._
import domain.base.Repository
import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import domain.eff.IdGen._idgen
import domain.shared.Id
import org.atnos.eff.Eff
import usecase.port.Port
import usecase.port.InputData
import usecase.port.OutputData

class DepositMoneyInteractor(
    val accountRepository: Repository[Account]
) extends Port[DepositMoneyInputData, DepositMoneyOutputData] {
  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  def execute[R: _idgen: _myErrorEither](in: DepositMoneyInputData): Eff[R, DepositMoneyOutputData] = for {
    before  <- accountRepository.resolve[R](Id[Account](in.id))
    after   <- before.deposit[R](Money(in.money))
    updated <- accountRepository.update[R](after)
  } yield DepositMoneyOutputData(payload = updated)
}

case class DepositMoneyInputData(principal: String, id: String, money: Long) extends InputData
case class DepositMoneyOutputData(payload: Account)                          extends OutputData
