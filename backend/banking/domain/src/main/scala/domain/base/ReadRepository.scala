package domain.base

import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import domain.error._
import domain.shared.Id
import org.atnos.eff.Eff
import org.atnos.eff.either._

trait ReadRepository[T <: AggregateRoot[T]] {
  def resolve[R: _authz: _myErrorEither](id: Id[T]): Eff[R, T] = for {
    maybe <- get[R](id)
    aggregateRoot <- fromEither[R, MyError, T](maybe match {
      case Some(v) => Right(v)
      case None    => Left(NotFoundAccountError(s"failed: $id"))
    })
  } yield aggregateRoot

  def get[R: _authz](id: Id[T]): Eff[R, Option[T]]
}
