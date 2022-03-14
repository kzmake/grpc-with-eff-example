package adapter.primary.grpc.banking.v1

import adapter.secondary.memory.AccountRepository
import api.banking._
import domain.account.{Money, Account}
import domain.shared.Id
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._
import usecase.interactor.{GetAccountInteractor, CreateAccountInteractor}

import scala.collection.concurrent.TrieMap

class BankingServiceControllerSpec extends AnyFreeSpec {
  "#createAccount" - {
    "OK" in {
      val datastore         = TrieMap.empty[Id[Account], Account]
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val service           = new BankingServiceController(createAccount, getAccount)

      val req = v1.CreateAccountRequest()
      val expected = v1.CreateAccountResponse(
        Some(
          v1.Account(
            id = """(\w){8}-(\w){4}-(\w){4}-(\w){4}-(\w){12}""", // random uuid
            balance = 100
          )
        )
      )

      val res = service.createAccount(req).futureValue

      res.account.get.id must fullyMatch regex expected.account.get.id
      res.account.get.balance mustBe expected.account.get.balance
    }
  }

  "#getAccount" - {
    "OK" in {
      val datastore = TrieMap[Id[Account], Account](
        Id[Account]("1234") -> Account(Id[Account]("1234"), Money(1234))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val service           = new BankingServiceController(createAccount, getAccount)

      val req = v1.GetAccountRequest(id = "1234")
      val expected = v1.GetAccountResponse(
        Some(
          v1.Account(
            id = "1234",
            balance = 1234
          )
        )
      )

      val res = service.getAccount(req).futureValue

      res mustBe expected
    }
  }
}
