package grpc

import adapter.primary.grpc.banking.v1.BankingServiceController
import adapter.secondary.memory.AccountRepository
import api.banking.v1._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.grpc.scaladsl.ServerReflection
import akka.grpc.scaladsl.ServiceHandler
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import domain.account.{Account, Money}
import domain.shared.Id
import usecase.interactor.{GetAccountInteractor, CreateAccountInteractor}

import scala.collection.concurrent.TrieMap
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object Main {
  def main(args: Array[String]): Unit = {
    val conf: Config = ConfigFactory
      .parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    implicit val sys: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "kzmake_banking", conf)
    implicit val ec: ExecutionContext      = sys.executionContext

    val datastore = TrieMap(
      Id[Account]("11111111-1111-1111-1111-111111111111") -> Account(
        Id[Account]("11111111-1111-1111-1111-111111111111"),
        Money(1000)
      )
    )
    val accountRepository      = new AccountRepository(datastore)
    val createAcountInteractor = new CreateAccountInteractor(accountRepository)
    val getAccountInteractor   = new GetAccountInteractor(accountRepository)

    val bankingService: PartialFunction[HttpRequest, Future[HttpResponse]] =
      BankingServicePowerApiHandler.partial(new BankingServiceController(createAcountInteractor, getAccountInteractor))
    val reflectionService: PartialFunction[HttpRequest, Future[HttpResponse]] =
      ServerReflection.partial(List(BankingService))
    val serviceHandlers: HttpRequest => Future[HttpResponse] =
      ServiceHandler.concatOrNotFound(bankingService, reflectionService)

    Http()
      .newServerAt("0.0.0.0", 50051)
      .bind(serviceHandlers)
      .foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }
  }
}
