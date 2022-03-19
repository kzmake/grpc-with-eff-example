package domain.account

import domain.eff.MyErrorEither.MyErrorEither
import domain.error._
import domain.shared.Id
import org.scalatest.matchers.must.Matchers._
import org.atnos.eff._
import org.atnos.eff.syntax.all._
import org.scalatest.freespec.AnyFreeSpec

class AccountSpec extends AnyFreeSpec {
  "#withdraw (課題1: お金の引き出し(WithdrawMoney)のAPIを実装)" - {

    "OK" - {

      "1000円の残高の口座から1000円引き出しできる" in {
        val account  = Account(Id[Account]("1"), Money(1000))
        val input    = Money(1000)
        val expected = Right(Account(Id[Account]("1"), Money(0)))

        type R = Fx.fx1[MyErrorEither]
        val actual =
          account
            .withdraw[R](input)
            .runEither[MyError]
            .run

        actual mustBe expected
      }

      "1000円の残高の口座から1円引き出しできる" in {
        val account  = Account(Id[Account]("1"), Money(1000))
        val input    = Money(1)
        val expected = Right(Account(Id[Account]("1"), Money(999)))

        type R = Fx.fx1[MyErrorEither]
        val actual =
          account
            .withdraw[R](input)
            .runEither[MyError]
            .run

        actual mustBe expected
      }

    }

    "KO" - {
      "サービスとして想定されない0円未満の預け入れはできない" in {
        val account  = Account(Id[Account]("1"), Money(1000))
        val input    = Money(0)
        val expected = "requirement failed: 引き出し額は1以上"

        type R = Fx.fx1[MyErrorEither]
        val thrown = the[IllegalArgumentException] thrownBy account.withdraw[R](input)
        thrown.getMessage mustBe expected
      }
    }

  }

  // 実装済み部分
  "#deposit" - {

    "OK" - {

      "1000円預け入れできる" in {
        val account  = Account(Id[Account]("1"), Money(1000))
        val input    = Money(1000)
        val expected = Right(Account(Id[Account]("1"), Money(2000)))

        type R = Fx.fx1[MyErrorEither]
        val actual =
          account
            .deposit[R](input)
            .runEither[MyError]
            .run

        actual mustBe expected
      }

      "1円預け入れできる" in {
        val account  = Account(Id[Account]("1"), Money(1000))
        val input    = Money(1)
        val expected = Right(Account(Id[Account]("1"), Money(1001)))

        type R = Fx.fx1[MyErrorEither]
        val actual =
          account
            .deposit[R](input)
            .runEither[MyError]
            .run

        actual mustBe expected
      }

      "100万円預け入れできる" in {
        val account  = Account(Id[Account]("1"), Money(1000))
        val input    = Money(1000000)
        val expected = Right(Account(Id[Account]("1"), Money(1001000)))

        type R = Fx.fx1[MyErrorEither]
        val actual =
          account
            .deposit[R](input)
            .runEither[MyError]
            .run

        actual mustBe expected
      }

    }

    "KO" - {

      "サービスとして想定されない0円未満の預け入れはできない" in {
        val account  = Account(Id[Account]("1"), Money(1000))
        val input    = Money(0)
        val expected = "requirement failed: 預け入れ額は1以上"

        type R = Fx.fx1[MyErrorEither]
        val thrown = the[IllegalArgumentException] thrownBy account.deposit[R](input)
        thrown.getMessage mustBe expected
      }

    }

  }
}
