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

class WithdrawMoneyInteractor(
    val accountRepository: Repository[Account]
) extends Port[WithdrawMoneyInputData, WithdrawMoneyOutputData] {
  def execute[R: _authz: _idgen: _myErrorEither](in: WithdrawMoneyInputData): Eff[R, WithdrawMoneyOutputData] = for {
    before  <- accountRepository.resolve[R](Id[Account](in.id))
    after   <- before.withdraw[R](Money(in.money))
    updated <- accountRepository.update[R](after)
  } yield WithdrawMoneyOutputData(payload = updated)
}

case class WithdrawMoneyInputData(id: String, money: Long) extends InputData
case class WithdrawMoneyOutputData(payload: Account)       extends OutputData
