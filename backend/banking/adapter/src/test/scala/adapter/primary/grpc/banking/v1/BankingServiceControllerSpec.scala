package adapter.primary.grpc.banking.v1

import adapter.secondary.memory.AccountRepository
import api.banking._
import akka.grpc.scaladsl.{Metadata, MetadataBuilder}
import domain.account.{Money, Account}
import domain.shared.Id
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._
import usecase.interactor.{GetAccountInteractor, CreateAccountInteractor}

import scala.collection.concurrent.TrieMap

class BankingServiceControllerSpec extends AnyFreeSpec {
  private val datastore = TrieMap(
    Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
    Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
  )

  "#createAccount" - {
    "OK" in {
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val service           = new BankingServiceController(createAccount, getAccount)

      val md  = new MetadataBuilder().addText("principal", "alice").build()
      val req = v1.CreateAccountRequest()
      val expected = v1.CreateAccountResponse(
        Some(
          v1.Account(
            id = """(\w){8}-(\w){4}-(\w){4}-(\w){4}-(\w){12}""", // random uuid
            balance = 100
          )
        )
      )

      val res = service.createAccount(req, md).futureValue

      res.account.get.id must fullyMatch regex expected.account.get.id
      res.account.get.balance mustBe expected.account.get.balance
    }
  }

  "#getAccount" - {
    "OK" in {
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val service           = new BankingServiceController(createAccount, getAccount)

      val md  = new MetadataBuilder().addText("principal", "alice").build()
      val req = v1.GetAccountRequest(id = "1")
      val expected = v1.GetAccountResponse(
        Some(
          v1.Account(
            id = "1",
            balance = 1000
          )
        )
      )

      val res = service.getAccount(req, md).futureValue

      res mustBe expected
    }
  }
}
