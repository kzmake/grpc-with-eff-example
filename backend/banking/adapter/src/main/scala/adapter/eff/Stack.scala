package adapter.eff

import domain.eff._
import org.atnos.eff._

object Stack {
  type AllStack = Fx.fx2[Authz, IdGen]
}
