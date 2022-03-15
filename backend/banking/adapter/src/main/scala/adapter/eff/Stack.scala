package adapter.eff

import domain.eff.MyErrorEither.MyErrorEither
import domain.eff._
import org.atnos.eff._

object Stack {
  type AStack = Fx.fx3[Authz, IdGen, MyErrorEither]
}
