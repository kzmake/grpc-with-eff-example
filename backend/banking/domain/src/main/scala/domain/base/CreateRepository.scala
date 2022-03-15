package domain.base

import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import org.atnos.eff.Eff

trait CreateRepository[T <: AggregateRoot[T]] {
  def add[R: _authz: _myErrorEither](aggregateRoot: T): Eff[R, T]
}
