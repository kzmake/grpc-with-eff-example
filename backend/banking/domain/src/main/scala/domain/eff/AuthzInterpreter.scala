package domain.eff

import org.atnos.eff._

trait AuthzInterpreter {
  def run[R, U, A](effects: Eff[R, A])(implicit m: Member.Aux[Authz, R, U]): Eff[U, A]
}
