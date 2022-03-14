package domain.account

import domain.base.AggregateRoot
import domain.eff.IdGen
import domain.eff.IdGen._idgen
import domain.shared.Id
import org.atnos.eff.Eff

final case class Account(id: Id[Account], balance: Money) extends AggregateRoot[Account] {
  assert(id.value.nonEmpty, "口座番号は空にならない")
  assert(balance >= Money.zero, "残高が0未満になることはない")

  def deposit(money: Money): Account = {
    require(Money.zero < money, "預け入れ額は1以上")
    copy(balance = balance + money)
      .ensuring(Money.zero <= _.balance, "残高が0未満になることはない")
  }
  def withdraw(money: Money): Account = {
    require(Money.zero < money && money <= balance, "引き落とせる額は0より大きく、残高と同額まで")
    copy(balance = balance - money)
      .ensuring(Money.zero <= _.balance, "残高が0未満になることはない")
  }
}

object Account {
  def applyEff[R: _idgen]: Eff[R, Account] = for {
    id <- IdGen.generate[Account, R]
  } yield Account(id = id, balance = Money(100))
}
