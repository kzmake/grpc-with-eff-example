package domain.base

import domain.eff.Authz._authz
import domain.shared.Id
import org.atnos.eff.Eff
import org.atnos.eff.syntax.all.toEffPureOps

trait ReadRepository[T <: AggregateRoot[T]] {
  def resolve[R: _authz](id: Id[T]): Eff[R, T] = for {
    maybe         <- get[R](id)
    aggregateRoot <- maybe.get.pureEff[R]
  } yield aggregateRoot

  def get[R: _authz](id: Id[T]): Eff[R, Option[T]]
}
