package adapter.primary.grpc.banking.v1

import api.banking.v1._
import adapter.eff.Stack._
import adapter.secondary.map.MapAuthzOps.AuthzOps
import adapter.secondary.uuid.UUIDIdGenOps.IdGenOps
import akka.grpc.GrpcServiceException
import akka.grpc.scaladsl.Metadata
import org.atnos.eff.syntax.all._
import usecase.interactor._
import usecase.port.Port
import domain.error.MyError
import io.grpc.Status
import scalapb.validate._

import scala.concurrent.Future

class BankingServiceController(
    val createAccountPort: Port[CreateAccountInputData, CreateAccountOutputData],
    val getAccountPort: Port[GetAccountInputData, GetAccountOutputData],
    val depositMoneyPort: Port[DepositMoneyInputData, DepositMoneyOutputData],
    val withdrawMoneyPort: Port[WithdrawMoneyInputData, WithdrawMoneyOutputData],
    val deleteAccountPort: Port[DeleteAccountInputData, DeleteAccountOutputData]
) extends BankingServicePowerApi {
  override def createAccount(req: CreateAccountRequest, metadata: Metadata): Future[CreateAccountResponse] = {
    Validator[CreateAccountRequest].validate(req) match {
      case Success =>
      case Failure(violations) =>
        throw new GrpcServiceException(
          Status.INVALID_ARGUMENT.withDescription(violations.mkString(": "))
        )
    }

    val principal = metadata.getText("principal").getOrElse("none")
    val in        = CreateAccountInputData()

    val out = createAccountPort
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
    Validator[GetAccountRequest].validate(req) match {
      case Success =>
      case Failure(violations) =>
        throw new GrpcServiceException(
          Status.INVALID_ARGUMENT.withDescription(violations.mkString(": "))
        )
    }

    val principal = metadata.getText("principal").getOrElse("none")
    val in        = GetAccountInputData(id = req.id)
    val out = getAccountPort
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

  override def depositMoney(req: DepositMoneyRequest, metadata: Metadata): Future[DepositMoneyResponse] = {
    Validator[DepositMoneyRequest].validate(req) match {
      case Success =>
      case Failure(violations) =>
        throw new GrpcServiceException(
          Status.INVALID_ARGUMENT.withDescription(violations.mkString(": "))
        )
    }

    val principal = metadata.getText("principal").getOrElse("none")
    val in        = DepositMoneyInputData(id = req.id, money = req.money)
    val out = depositMoneyPort
      .execute[AStack](in)
      .runAuthz(principal)
      .runIdGen
      .runEither[MyError]
      .run

    out match {
      case Right(v) =>
        Future.successful(
          DepositMoneyResponse(
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

  override def withdrawMoney(req: WithdrawMoneyRequest, metadata: Metadata): Future[WithdrawMoneyResponse] = {
    Validator[WithdrawMoneyRequest].validate(req) match {
      case Success =>
      case Failure(violations) =>
        throw new GrpcServiceException(
          Status.INVALID_ARGUMENT.withDescription(violations.mkString(": "))
        )
    }

    val principal = metadata.getText("principal").getOrElse("none")
    val in        = WithdrawMoneyInputData(id = req.id, money = req.money)
    val out = withdrawMoneyPort
      .execute[AStack](in)
      .runAuthz(principal)
      .runIdGen
      .runEither[MyError]
      .run

    out match {
      case Right(v) =>
        Future.successful(
          WithdrawMoneyResponse(
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

  override def deleteAccount(req: DeleteAccountRequest, metadata: Metadata): Future[DeleteAccountResponse] = {
    Validator[DeleteAccountRequest].validate(req) match {
      case Success =>
      case Failure(violations) =>
        throw new GrpcServiceException(
          Status.INVALID_ARGUMENT.withDescription(violations.mkString(": "))
        )
    }

    val principal = metadata.getText("principal").getOrElse("none")
    val in        = DeleteAccountInputData(id = req.id)
    val out = deleteAccountPort
      .execute[AStack](in)
      .runAuthz(principal)
      .runIdGen
      .runEither[MyError]
      .run

    out match {
      case Right(v) => Future.successful(DeleteAccountResponse())
      case Left(e)  => Future.failed(e)
    }
  }
}
