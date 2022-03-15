package domain.base

import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import org.atnos.eff.Eff

trait UpdateRepository[T <: AggregateRoot[T]] {
  def update[R: _authz: _myErrorEither](aggregateRoot: T): Eff[R, T]
}
