package adapter.secondary.memory

import domain.account.Account
import domain.base.Repository
import domain.shared.Id
import org.atnos.eff.Eff
import org.atnos.eff.syntax.all.toEffPureOps

import scala.collection.concurrent.TrieMap

class AccountRepository(
    val datastore: TrieMap[Id[Account], Account]
) extends Repository[Account] {
  override def add[R](aggregateRoot: Account): Eff[R, Account] = {
    datastore.addOne(aggregateRoot.id, aggregateRoot)

    resolve[R](aggregateRoot.id)
  }

  override def update[R](aggregateRoot: Account): Eff[R, Account] = {
    datastore.update(aggregateRoot.id, aggregateRoot)

    resolve[R](aggregateRoot.id)
  }

  override def get[R](id: Id[Account]): Eff[R, Option[Account]] = datastore.get(id).pureEff[R]

  override def remove[R](aggregateRoot: Account): Eff[R, Id[Account]] = {
    datastore.remove(aggregateRoot.id, aggregateRoot)

    aggregateRoot.id.pureEff[R]
  }
}
