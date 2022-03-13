package adapter.eff

import org.atnos.eff.{Fx, TimedFuture}

object Stack {
  type CommandAllStack = Fx.fx1[TimedFuture]
}
