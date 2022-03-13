package adapter.primary.grpc.banking.v1

import adapter.secondary.memory.AccountRepository
import api.banking._
import domain.account.Account
import domain.shared.Id
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._
import usecase.interactor.CreateAccountInteractor
g
import scala.collection.concurrent.TrieMap

class BankingServiceControllerSpec extends AnyFreeSpec {
  "#createAccount" - {
    "OK" in {
      var datastore         = TrieMap.empty[Id[Account], Account]
      val accountRepository = new AccountRepository(datastore)
      val create            = new CreateAccountInteractor(accountRepository)
      val service           = new BankingServiceController(create)

      val req = v1.CreateAccountRequest()
      val expected = v1.CreateAccountResponse(
        Some(
          v1.Account(
            id = "1",
            balance = 0
          )
        )
      )

      val res = service.createAccount(req).futureValue

      res mustBe expected
    }
  }
}
