package domain.base

import domain.shared.Id
import org.atnos.eff.Eff

trait DeleteRepository[T <: AggregateRoot[T]] {
  def remove[R](aggregateRoot: T): Eff[R, Id[T]]
}
