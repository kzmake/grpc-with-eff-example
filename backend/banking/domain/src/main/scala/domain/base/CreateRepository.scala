package domain.base

import org.atnos.eff.Eff

trait CreateRepository[T <: AggregateRoot[T]] {
  def add[R](aggregateRoot: T): Eff[R, T]
}
