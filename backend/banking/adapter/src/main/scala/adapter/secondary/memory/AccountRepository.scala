package adapter.secondary.memory

import domain.account.Account
import domain.base.Repository
import domain.eff.Authz
import domain.eff.Authz._authz
import domain.eff.MyErrorEither._myErrorEither
import domain.shared.Id
import org.atnos.eff.Eff
import org.atnos.eff.syntax.all.toEffPureOps

import scala.collection.concurrent.TrieMap

// TODO: 課題3: AuthZ(認可)エフェクトの実装 / アダプター層: インタープリターの実装
class AccountRepository(
    val datastore: TrieMap[Id[Account], Account]
) extends Repository[Account] {
  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  override def add[R: _myErrorEither](aggregateRoot: Account): Eff[R, Account] = for {
    // TODO: 課題3: 作成するIDの認可を割り当てたい

    _ <- datastore.addOne(aggregateRoot.id, aggregateRoot).pureEff[R]
    a <- resolve[R](aggregateRoot.id)
  } yield a

  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  override def update[R: _myErrorEither](aggregateRoot: Account): Eff[R, Account] = for {
    // TODO: 課題3: 更新するIDの認可を要求したい

    _ <- datastore.update(aggregateRoot.id, aggregateRoot).pureEff[R]
    a <- resolve[R](aggregateRoot.id)
  } yield a

  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  override def get[R](id: Id[Account]): Eff[R, Option[Account]] = for {
    // TODO: 課題3: 取得するIDの認可を要求したい

    a <- datastore.get(id).pureEff[R]
  } yield a

  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / _authz 追加
  override def remove[R: _myErrorEither](aggregateRoot: Account): Eff[R, Unit] = for {
    // TODO: 課題3: 削除するIDの認可を要求したい

    _ <- datastore.remove(aggregateRoot.id, aggregateRoot).pureEff[R]
  } yield ()
}
