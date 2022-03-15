package domain.base

import domain.eff.Authz._authz
import org.atnos.eff.Eff

trait UpdateRepository[T <: AggregateRoot[T]] {
  def update[R: _authz](aggregateRoot: T): Eff[R, T]
}
