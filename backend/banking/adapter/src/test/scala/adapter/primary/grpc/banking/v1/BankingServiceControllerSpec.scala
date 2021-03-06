package adapter.primary.grpc.banking.v1

import adapter.secondary.memory.AccountRepository
import akka.grpc.GrpcServiceException
import api.banking._
import akka.grpc.scaladsl.MetadataBuilder
import domain.account._
import domain.shared.Id
import io.grpc.Status
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._
import usecase.interactor._

import scala.collection.concurrent.TrieMap

class BankingServiceControllerSpec extends AnyFreeSpec {
  "#createAccount" - {
    "OK: aliceが口座を作成できる (課題2: IdGen(Id生成)エフェクトのインタープリター実装)" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md  = new MetadataBuilder().addText("principal", "alice").build()
      val req = v1.CreateAccountRequest()
      val expected = v1.CreateAccountResponse(
        Some(
          v1.Account(
            id = """(\w){8}-(\w){4}-(\w){4}-(\w){4}-(\w){12}""", // random uuid
            balance = 0
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
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

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
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

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
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

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
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md  = new MetadataBuilder().addText("principal", "bob").build()
      val req = v1.GetAccountRequest(id = "1")
      val expected = new GrpcServiceException(
        Status.UNAUTHENTICATED.withDescription("認可に失敗しました: bob の Set(2) に Set(1) が含まれていない")
      )

      val e = service.getAccount(req, md).failed.futureValue
      e.getMessage mustBe expected.getMessage
    }

    "KO: bobがaliceの作成した口座を取得できない" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md = new MetadataBuilder().addText("principal", "bob").build()

      // created
      val createRes = service
        .createAccount(v1.CreateAccountRequest(), new MetadataBuilder().addText("principal", "alice").build())
        .futureValue

      val req = v1.GetAccountRequest(id = createRes.getAccount.id)
      val expected = new GrpcServiceException(
        Status.UNAUTHENTICATED.withDescription(s"認可に失敗しました: bob の Set(2) に Set(${createRes.getAccount.id}) が含まれていない")
      )

      val e = service.getAccount(req, md).failed.futureValue
      e.getMessage mustBe expected.getMessage
    }

  }

  "#depositMoney" - {
    "OK: aliceが口座に預け入れできる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md  = new MetadataBuilder().addText("principal", "alice").build()
      val req = v1.DepositMoneyRequest(id = "1", money = 10000)
      val expected = v1.DepositMoneyResponse(
        Some(
          v1.Account(
            id = "1",
            balance = 11000
          )
        )
      )

      val res = service.depositMoney(req, md).futureValue

      res mustBe expected
    }

    "OK: aliceが作成した口座に預け入れできる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md = new MetadataBuilder().addText("principal", "alice").build()

      // created
      val createRes = service.createAccount(v1.CreateAccountRequest(), md).futureValue

      val req = v1.DepositMoneyRequest(id = createRes.getAccount.id, money = 10000)
      val expected = v1.DepositMoneyResponse(
        Some(
          v1.Account(
            id = createRes.getAccount.id,
            balance = createRes.getAccount.balance + 10000
          )
        )
      )

      val res = service.depositMoney(req, md).futureValue

      res mustBe expected
    }

    "OK: bobが口座に預け入れできる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md  = new MetadataBuilder().addText("principal", "bob").build()
      val req = v1.DepositMoneyRequest(id = "2", money = 10000)
      val expected = v1.DepositMoneyResponse(
        Some(
          v1.Account(
            id = "2",
            balance = 10999
          )
        )
      )

      val res = service.depositMoney(req, md).futureValue

      res mustBe expected
    }

    "KO: bobがaliceの口座に預け入れできない" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md  = new MetadataBuilder().addText("principal", "bob").build()
      val req = v1.DepositMoneyRequest(id = "1", money = 10000)
      val expected = new GrpcServiceException(
        Status.UNAUTHENTICATED.withDescription("認可に失敗しました: bob の Set(2) に Set(1) が含まれていない")
      )

      val e = service.depositMoney(req, md).failed.futureValue
      e.getMessage mustBe expected.getMessage
    }

    "KO: bobがaliceの作成した口座に預け入れできない" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md = new MetadataBuilder().addText("principal", "bob").build()

      // created
      val createRes = service
        .createAccount(v1.CreateAccountRequest(), new MetadataBuilder().addText("principal", "alice").build())
        .futureValue

      val req = v1.DepositMoneyRequest(id = createRes.getAccount.id, money = 10000)
      val expected = new GrpcServiceException(
        Status.UNAUTHENTICATED.withDescription(s"認可に失敗しました: bob の Set(2) に Set(${createRes.getAccount.id}) が含まれていない")
      )

      val e = service.depositMoney(req, md).failed.futureValue
      e.getMessage mustBe expected.getMessage
    }

  }

  "#withdrawMoney (課題1: お金の引き出し(WithdrawMoney)のAPIを実装)" - {
    "OK: aliceが口座から引き出しできる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md  = new MetadataBuilder().addText("principal", "alice").build()
      val req = v1.WithdrawMoneyRequest(id = "1", money = 999)
      val expected = v1.WithdrawMoneyResponse(
        Some(
          v1.Account(
            id = "1",
            balance = 1
          )
        )
      )

      val res = service.withdrawMoney(req, md).futureValue

      res mustBe expected
    }

    "OK: bobが口座から引き出しできる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md  = new MetadataBuilder().addText("principal", "bob").build()
      val req = v1.WithdrawMoneyRequest(id = "2", money = 999)
      val expected = v1.WithdrawMoneyResponse(
        Some(
          v1.Account(
            id = "2",
            balance = 0
          )
        )
      )

      val res = service.withdrawMoney(req, md).futureValue

      res mustBe expected
    }

  }

  "#deleteAccount" - {
    "OK: aliceが口座を削除できる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md       = new MetadataBuilder().addText("principal", "alice").build()
      val req      = v1.DeleteAccountRequest(id = "1")
      val expected = v1.DeleteAccountResponse()

      val res = service.deleteAccount(req, md).futureValue

      res mustBe expected
    }

    "OK: aliceが作成した口座を削除できる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md = new MetadataBuilder().addText("principal", "alice").build()

      // created
      val createRes = service.createAccount(v1.CreateAccountRequest(), md).futureValue

      val req      = v1.DeleteAccountRequest(id = createRes.getAccount.id)
      val expected = v1.DeleteAccountResponse()

      val res = service.deleteAccount(req, md).futureValue

      res mustBe expected
    }

    "OK: bobが口座を削除できる" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md       = new MetadataBuilder().addText("principal", "bob").build()
      val req      = v1.DeleteAccountRequest(id = "2")
      val expected = v1.DeleteAccountResponse()

      val res = service.deleteAccount(req, md).futureValue

      res mustBe expected
    }

    "KO: bobがaliceの口座を削除できない" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md  = new MetadataBuilder().addText("principal", "bob").build()
      val req = v1.DeleteAccountRequest(id = "1")
      val expected = new GrpcServiceException(
        Status.UNAUTHENTICATED.withDescription("認可に失敗しました: bob の Set(2) に Set(1) が含まれていない")
      )

      val e = service.deleteAccount(req, md).failed.futureValue
      e.getMessage mustBe expected.getMessage
    }

    "KO: bobがaliceの作成した口座を削除できない" in {
      val datastore = TrieMap(
        Id[Account]("1") -> Account(id = Id[Account]("1"), balance = Money(1000)),
        Id[Account]("2") -> Account(id = Id[Account]("2"), balance = Money(999))
      )
      val accountRepository = new AccountRepository(datastore)
      val createAccount     = new CreateAccountInteractor(accountRepository)
      val getAccount        = new GetAccountInteractor(accountRepository)
      val depositMoney      = new DepositMoneyInteractor(accountRepository)
      val withdrawMoney     = new WithdrawMoneyInteractor(accountRepository)
      val deleteAccount     = new DeleteAccountInteractor(accountRepository)
      val service = new BankingServiceController(
        createAccount,
        getAccount,
        depositMoney,
        withdrawMoney,
        deleteAccount
      )

      val md = new MetadataBuilder().addText("principal", "bob").build()

      // created
      val createRes = service
        .createAccount(v1.CreateAccountRequest(), new MetadataBuilder().addText("principal", "alice").build())
        .futureValue

      val req = v1.DeleteAccountRequest(id = createRes.getAccount.id)
      val expected = new GrpcServiceException(
        Status.UNAUTHENTICATED.withDescription(s"認可に失敗しました: bob の Set(2) に Set(${createRes.getAccount.id}) が含まれていない")
      )

      val e = service.deleteAccount(req, md).failed.futureValue
      e.getMessage mustBe expected.getMessage
    }

  }
}
