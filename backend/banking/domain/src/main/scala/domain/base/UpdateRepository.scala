package domain.base

import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import org.atnos.eff.Eff

trait UpdateRepository[T <: AggregateRoot[T]] {
  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  def update[R: _myErrorEither](aggregateRoot: T): Eff[R, T]
}
