package domain.eff

import domain.eff.MyErrorEither._myErrorEither
import org.atnos.eff._

trait AuthzInterpreter {
  def run[R, U, A](principal: String, effects: Eff[R, A])(implicit
      m: Member.Aux[Authz, R, U],
      m1: _myErrorEither[U]
  ): Eff[U, A]
}
