package adapter.primary.grpc.banking.v1

import api.banking.v1._
import adapter.eff.Stack._
import adapter.secondary.map.MapAuthzOps.AuthzOps
import adapter.secondary.uuid.UUIDIdGenOps.IdGenOps
import akka.grpc.scaladsl.Metadata
import org.atnos.eff.syntax.all._
import usecase.interactor._
import usecase.port.Port

import scala.concurrent.Future

class BankingServiceController(
    val createAccount: Port[CreateAccountInputData, CreateAccountOutputData],
    val getAccount: Port[GetAccountInputData, GetAccountOutputData]
) extends BankingServicePowerApi {
  override def createAccount(req: CreateAccountRequest, metadata: Metadata): Future[CreateAccountResponse] = {
    val principal = metadata.getText("principal").getOrElse("")
    val in        = CreateAccountInputData()
    val out =
      createAccount
        .execute[AllStack](in)
        .runAuthz(principal)
        .runIdGen
        .runPure

    out match {
      case Some(x) =>
        Future.successful(
          CreateAccountResponse(
            Some(
              Account(
                id = x.payload.id.value,
                balance = x.payload.balance.value
              )
            )
          )
        )
      case None => Future.failed(new Exception("failed"))
    }
  }

  override def getAccount(req: GetAccountRequest, metadata: Metadata): Future[GetAccountResponse] = {
    val principal = metadata.getText("principal").getOrElse("")
    val in        = GetAccountInputData(id = req.id)
    val out =
      getAccount
        .execute[AllStack](in)
        .runAuthz(principal)
        .runPure

    out match {
      case Some(x) =>
        Future.successful(
          GetAccountResponse(
            Some(
              Account(
                id = x.payload.id.value,
                balance = x.payload.balance.value
              )
            )
          )
        )
      case None => Future.failed(new Exception("failed"))
    }
  }

  override def depositMoney(req: DepositMoneyRequest, metadata: Metadata): Future[DepositMoneyResponse] = ???

  override def withdrawMoney(req: WithdrawMoneyRequest, metadata: Metadata): Future[WithdrawMoneyResponse] = ???

  override def deleteAccount(req: DeleteAccountRequest, metadata: Metadata): Future[DeleteAccountResponse] = ???
}
