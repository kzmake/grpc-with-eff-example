package adapter.eff

import domain.eff.IdGen
import org.atnos.eff._

object Stack {
  type AllStack = Fx.fx1[IdGen]
}
