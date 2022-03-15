package domain.eff

import domain.account.Account
import domain.shared.Id
import org.atnos.eff.Interpret.translate
import org.atnos.eff._
import org.atnos.eff.syntax.all._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._

import java.util.UUID

class IdGenSpec extends AnyFreeSpec {
  private val frozen = UUID.randomUUID().toString

  object TestIdGenOps extends IdGenInterpreter {
    implicit class IdGenOps[R, A](effects: Eff[R, A]) {
      def runIdGen[U](implicit
          m: Member.Aux[IdGen, R, U]
      ): Eff[U, A] = run(() => frozen, effects)
    }

    def run[R, U, A](generator: () => String, effects: Eff[R, A])(implicit
        m: Member.Aux[IdGen, R, U]
    ): Eff[U, A] = {
      translate(effects)(new Translate[IdGen, U] {
        def apply[X](a: IdGen[X]): Eff[U, X] =
          a match {
            case v: Generate[_] => Id[X](generator()).asInstanceOf[X].pureEff[U]
          }
      })
    }
  }

  "#generate" - {
    "IDを生成できる" in {
      import TestIdGenOps._

      type R = Fx.fx1[IdGen]
      val actual = (
        for {
          id <- IdGen.generate[Account, R]
        } yield id
      ).runIdGen.runPure.get

      actual mustBe Id[Account](frozen)
    }
  }
}
