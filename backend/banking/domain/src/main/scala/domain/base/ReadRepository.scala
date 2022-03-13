package domain.base

import domain.shared.Id
import org.atnos.eff.Eff
import org.atnos.eff.syntax.all.toEffPureOps

trait ReadRepository[T <: AggregateRoot[T]] {
  def get[R](id: Id[T]): Eff[R, T] = for {
    maybe         <- find[R](id)
    aggregateRoot <- maybe.get.pureEff[R]
  } yield aggregateRoot

  def find[R](id: Id[T]): Eff[R, Option[T]]
}
