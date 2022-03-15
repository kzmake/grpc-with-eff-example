package adapter.secondary.memory

import domain.account.Account
import domain.base.Repository
import domain.eff.Authz
import domain.eff.Authz._authz
import domain.shared.Id
import org.atnos.eff.Eff
import org.atnos.eff.syntax.all.toEffPureOps

import scala.collection.concurrent.TrieMap

class AccountRepository(
    val datastore: TrieMap[Id[Account], Account]
) extends Repository[Account] {
  override def add[R: _authz](aggregateRoot: Account): Eff[R, Account] = {
    for {
      _ <- datastore.addOne(aggregateRoot.id, aggregateRoot).pureEff[R]
      a <- resolve[R](aggregateRoot.id)
    } yield a
  }

  override def update[R: _authz](aggregateRoot: Account): Eff[R, Account] = for {
    _ <- Authz.require(aggregateRoot.id.resourceScope)

    _ <- datastore.update(aggregateRoot.id, aggregateRoot).pureEff[R]
    a <- resolve[R](aggregateRoot.id)
  } yield a

  override def get[R: _authz](id: Id[Account]): Eff[R, Option[Account]] = for {
    _ <- Authz.require(id.resourceScope)

    a <- datastore.get(id).pureEff[R]
  } yield a

  override def remove[R: _authz](aggregateRoot: Account): Eff[R, Unit] = for {
    _ <- Authz.require(aggregateRoot.id.resourceScope)

    _ <- datastore.remove(aggregateRoot.id, aggregateRoot).pureEff[R]
  } yield ()
}
