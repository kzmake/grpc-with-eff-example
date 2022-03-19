package domain.base

import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import domain.error._
import domain.shared.Id
import org.atnos.eff.Eff
import org.atnos.eff.either._

trait ReadRepository[T <: AggregateRoot[T]] {
  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  def resolve[R: _myErrorEither](id: Id[T]): Eff[R, T] = for {
    maybe <- get[R](id)
    aggregateRoot <- fromEither[R, MyError, T](maybe match {
      case Some(v) => Right(v)
      case None    => Left(NotFoundError(s"リソースが見つかりませんでした: $id"))
    })
  } yield aggregateRoot

  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  def get[R](id: Id[T]): Eff[R, Option[T]]
}
