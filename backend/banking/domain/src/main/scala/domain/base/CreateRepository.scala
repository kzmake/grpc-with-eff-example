package domain.base

import domain.eff.Authz._authz
import org.atnos.eff.Eff

trait CreateRepository[T <: AggregateRoot[T]] {
  def add[R: _authz](aggregateRoot: T): Eff[R, T]
}
