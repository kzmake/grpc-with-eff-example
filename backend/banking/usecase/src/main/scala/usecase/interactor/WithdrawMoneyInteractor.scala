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
  // TODO: 課題1: お金の引き出し(WithdrawMoney)のAPIを実装 / ユースケース層: WithdrawMoneyInteractorの実装
  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  def execute[R: _idgen: _myErrorEither](in: WithdrawMoneyInputData): Eff[R, WithdrawMoneyOutputData] = ???
}

case class WithdrawMoneyInputData(principal: String, id: String, money: Long) extends InputData
case class WithdrawMoneyOutputData(payload: Account)                          extends OutputData
