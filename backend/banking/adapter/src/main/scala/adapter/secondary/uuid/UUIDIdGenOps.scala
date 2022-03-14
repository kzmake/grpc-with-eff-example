package adapter.secondary.uuid

import java.util.UUID
import domain.shared.Id
import org.atnos.eff.Interpret.translate
import domain.eff._
import org.atnos.eff._
import org.atnos.eff.syntax.all._

object UUIDIdGenOps extends IdGenInterpreter {
  implicit class IdGenOps[R, A](effects: Eff[R, A]) {
    def runIdGen[U](implicit
        m: Member.Aux[IdGen, R, U]
    ): Eff[U, A] = run(() => UUID.randomUUID().toString, effects)
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
