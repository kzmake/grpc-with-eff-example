package domain.base

import org.atnos.eff.Eff

trait UpdateRepository[T <: AggregateRoot[T]] {
  def update[R](aggregateRoot: T): Eff[R, T]
}
