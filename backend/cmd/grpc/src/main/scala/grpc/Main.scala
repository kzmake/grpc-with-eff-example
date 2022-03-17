package grpc

import adapter.primary.grpc.banking.v1.BankingServiceController
import adapter.secondary.memory.AccountRepository
import api.banking._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.grpc.scaladsl.ServerReflection
import akka.grpc.scaladsl.ServiceHandler
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import domain.account
import domain.account._
import domain.shared.Id
import usecase.interactor._

import scala.collection.concurrent.TrieMap
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object Main {
  def main(args: Array[String]): Unit = {
    val conf: Config = ConfigFactory
      .parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    implicit val sys: ActorSystem[Nothing] = ActorSystem[Nothing](Behaviors.empty, "kzmake_banking", conf)
    implicit val ec: ExecutionContext      = sys.executionContext

    val datastore = TrieMap(
      Id[Account]("11111111-1111-1111-1111-111111111111") -> Account(
        Id[Account]("11111111-1111-1111-1111-111111111111"),
        Money(1000)
      )
    )
    val accountRepository       = new AccountRepository(datastore)
    val createAcountInteractor  = new CreateAccountInteractor(accountRepository)
    val getAccountInteractor    = new GetAccountInteractor(accountRepository)
    val depositMoneyInteractor  = new DepositMoneyInteractor(accountRepository)
    val withdrawMoneyInteractor = new WithdrawMoneyInteractor(accountRepository)
    val deleteAccountInteractor = new DeleteAccountInteractor(accountRepository)

    val bankingService: PartialFunction[HttpRequest, Future[HttpResponse]] =
      v1.BankingServicePowerApiHandler.partial(
        new BankingServiceController(
          createAcountInteractor,
          getAccountInteractor,
          depositMoneyInteractor,
          withdrawMoneyInteractor,
          deleteAccountInteractor
        )
      )
    val reflectionService: PartialFunction[HttpRequest, Future[HttpResponse]] =
      ServerReflection.partial(List(v1.BankingService))
    val serviceHandlers: HttpRequest => Future[HttpResponse] =
      ServiceHandler.concatOrNotFound(bankingService, reflectionService)

    Http()
      .newServerAt("0.0.0.0", 50051)
      .bind(serviceHandlers)
      .foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }
  }
}
