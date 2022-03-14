package domain.eff

import org.atnos.eff._

trait IdGenInterpreter {
  def run[R, U, A](generator: () => String, effects: Eff[R, A])(implicit m: Member.Aux[IdGen, R, U]): Eff[U, A]
}
