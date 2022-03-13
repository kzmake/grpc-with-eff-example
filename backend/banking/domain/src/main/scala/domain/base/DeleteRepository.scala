package domain.base

import org.atnos.eff.Eff

trait DeleteRepository[T <: AggregateRoot[T]] {
  def delete[R](aggregateRoot: T): Eff[R, T]
}
