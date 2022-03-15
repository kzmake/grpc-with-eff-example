package adapter.primary.grpc.banking.v1

import adapter.secondary.memory.AccountRepository
import api.banking._
import akka.grpc.scaladsl.MetadataBuilder
import domain.account.{Money, Account}
import domain.error.UnauthorizedError
import domain.shared.Id
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._
import usecase.interactor.{GetAccountInteractor, CreateAccountInteractor}

import scala.collection.concurrent.TrieMap

class BankingServiceControllerSpec extends AnyFreeSpec {
  "#createAccount" - {
    "OK: aliceが口座を作成できる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
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
    "OK: aliceが口座を取得できる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
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

    "OK: aliceが作成した口座を取得できる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val service           = new BankingServiceController(createAccount, getAccount)

      val md = new MetadataBuilder().addText("principal", "alice").build()

      // created
      val createRes = service.createAccount(v1.CreateAccountRequest(), md).futureValue

      val req = v1.GetAccountRequest(id = createRes.getAccount.id)
      val expected = v1.GetAccountResponse(
        Some(
          v1.Account(
            id = createRes.getAccount.id,
            balance = createRes.getAccount.balance
          )
        )
      )

      val res = service.getAccount(req, md).futureValue

      res mustBe expected
    }

    "OK: bobが口座を取得できる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val service           = new BankingServiceController(createAccount, getAccount)

      val md  = new MetadataBuilder().addText("principal", "bob").build()
      val req = v1.GetAccountRequest(id = "2")
      val expected = v1.GetAccountResponse(
        Some(
          v1.Account(
            id = "2",
            balance = 999
          )
        )
      )

      val res = service.getAccount(req, md).futureValue

      res mustBe expected
    }

    "KO: bobがaliceの口座を取得できない" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val service           = new BankingServiceController(createAccount, getAccount)

      val md       = new MetadataBuilder().addText("principal", "bob").build()
      val req      = v1.GetAccountRequest(id = "1")
      val expected = UnauthorizedError("認可に失敗しました: Set(2) に Set(1) が含まれていない")

      val e = service.getAccount(req, md).failed.futureValue
      e mustBe expected
    }

    "OK: bobがaliceの作成した口座を取得できない" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val service           = new BankingServiceController(createAccount, getAccount)

      val md = new MetadataBuilder().addText("principal", "bob").build()

      // created
      val createRes = service
        .createAccount(v1.CreateAccountRequest(), new MetadataBuilder().addText("principal", "alice").build())
        .futureValue

      val req      = v1.GetAccountRequest(id = createRes.getAccount.id)
      val expected = UnauthorizedError(s"認可に失敗しました: Set(2) に Set(${createRes.getAccount.id}) が含まれていない")

      val e = service.getAccount(req, md).failed.futureValue
      e mustBe expected
    }

  }
}
