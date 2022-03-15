package domain.base

import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import org.atnos.eff.Eff

trait DeleteRepository[T <: AggregateRoot[T]] {
  def remove[R: _authz: _myErrorEither](aggregateRoot: T): Eff[R, Unit]
}
