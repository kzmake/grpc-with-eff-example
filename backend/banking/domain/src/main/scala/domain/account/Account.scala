package domain.account

import domain.base.Entity
import domain.shared.Id
import org.atnos.eff.Eff
import org.atnos.eff.syntax.all.toEffPureOps

final case class Account(id: Id[Account], balance: Money) extends Entity[Account] {
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
  def applyEff[R]: Eff[R, Account] = Account(id = Id[Account]("1"), balance = Money(0)).pureEff[R]
}
