package domain.account

import domain.base.AggregateRoot
import domain.eff.IdGen
import domain.eff.IdGen._idgen
import domain.eff.MyErrorEither._myErrorEither
import domain.error._
import domain.shared.Id
import org.atnos.eff.Eff
import org.atnos.eff.all.fromEither

final case class Account(id: Id[Account], balance: Money) extends AggregateRoot[Account] {
  assert(id.value.nonEmpty, "口座番号は空にならない")
  assert(balance >= Money.zero, "残高が0未満になることはない")

  def deposit[R: _myErrorEither](money: Money): Eff[R, Account] = {
    require(Money.zero < money, "預け入れ額は1以上")

    for {
      // NOTE: ビジネス的な状況にもよるが、ドメイン的に Either[Right, Account] で扱いたい可能性が高いという例でfromEitherしちゃう
      account <- fromEither[R, MyError, Account](Right(copy(balance = balance + money)))
    } yield account
  }

  def withdraw[R: _myErrorEither](money: Money): Eff[R, Account] = {
    require(Money.zero < money, "引き出し額は1以上")

    for {
      account <- fromEither[R, MyError, Account](
        if (money <= this.balance) Right(copy(balance = balance - money))
        else Left(StateError("残高が不足しています"))
      )
    } yield account
  }
}

object Account {
  def applyEff[R: _idgen]: Eff[R, Account] = for {
    id <- IdGen.generate[Account, R]
  } yield Account(id = id, balance = Money.zero)

  def applyEff[R: _idgen: _myErrorEither](balance: Money): Eff[R, Account] = for {
    id <- IdGen.generate[Account, R]
    validId <- fromEither[R, MyError, Id[Account]](
      if (id.value.nonEmpty) Right(id)
      else Left(InvalidError("口座番号は空にできません"))
    )
    validBalance <- fromEither[R, MyError, Money](
      if (Money.zero <= balance) Right(balance)
      else Left(InvalidError("残高は0未満にできません"))
    )
  } yield Account(id = validId, balance = validBalance)

  def applyEff[R: _myErrorEither](id: Id[Account], balance: Money): Eff[R, Account] = for {
    validId <- fromEither[R, MyError, Id[Account]](
      if (id.value.nonEmpty) Right(id)
      else Left(InvalidError("口座番号は空にできません"))
    )
    validBalance <- fromEither[R, MyError, Money](
      if (Money.zero <= balance) Right(balance)
      else Left(InvalidError("残高は0未満にできません"))
    )
  } yield Account(id = validId, balance = validBalance)
}
