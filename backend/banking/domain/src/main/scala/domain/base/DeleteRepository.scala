package domain.base

import domain.eff.Authz._authz
import org.atnos.eff.Eff

trait DeleteRepository[T <: AggregateRoot[T]] {
  def remove[R: _authz](aggregateRoot: T): Eff[R, Unit]
}
