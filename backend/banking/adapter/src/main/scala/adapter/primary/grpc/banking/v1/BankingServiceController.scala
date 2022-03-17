package adapter.primary.grpc.banking.v1

import api.banking.v1._
import adapter.eff.Stack._
import adapter.secondary.map.MapAuthzOps.AuthzOps
import adapter.secondary.uuid.UUIDIdGenOps.IdGenOps
import akka.grpc.scaladsl.Metadata
import org.atnos.eff.syntax.all._
import usecase.interactor._
import usecase.port.Port
import domain.error.MyError

import scala.concurrent.Future

class BankingServiceController(
    val createAccount: Port[CreateAccountInputData, CreateAccountOutputData],
    val getAccount: Port[GetAccountInputData, GetAccountOutputData]
) extends BankingServicePowerApi {
  override def createAccount(req: CreateAccountRequest, metadata: Metadata): Future[CreateAccountResponse] = {
    val principal = metadata.getText("principal").getOrElse("none")
    val in        = CreateAccountInputData()

    val out = createAccount
      .execute[AStack](in)
      .runAuthz(principal)
      .runIdGen
      .runEither[MyError]
      .run

    out match {
      case Right(v) =>
        Future.successful(
          CreateAccountResponse(
            Some(
              Account(
                id = v.payload.id.value,
                balance = v.payload.balance.value
              )
            )
          )
        )
      case Left(e) => Future.failed(e)
    }
  }

  override def getAccount(req: GetAccountRequest, metadata: Metadata): Future[GetAccountResponse] = {
    val principal = metadata.getText("principal").getOrElse("none")
    val in        = GetAccountInputData(id = req.id)
    val out = getAccount
      .execute[AStack](in)
      .runAuthz(principal)
      .runIdGen
      .runEither[MyError]
      .run

    out match {
      case Right(v) =>
        Future.successful(
          GetAccountResponse(
            Some(
              Account(
                id = v.payload.id.value,
                balance = v.payload.balance.value
              )
            )
          )
        )
      case Left(e) => Future.failed(e)
    }
  }

  override def depositMoney(req: DepositMoneyRequest, metadata: Metadata): Future[DepositMoneyResponse] = ???

  override def withdrawMoney(req: WithdrawMoneyRequest, metadata: Metadata): Future[WithdrawMoneyResponse] = ???

  override def deleteAccount(req: DeleteAccountRequest, metadata: Metadata): Future[DeleteAccountResponse] = ???
}
