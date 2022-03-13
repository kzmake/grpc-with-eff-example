package usecase.interactor

import domain.account.Account
import org.atnos.eff.Eff
import usecase.port.Port
import usecase.port.InputData
import usecase.port.OutputData

class CreateAccountInteractor extends Port[CreateAccountInputData, CreateAccountOutputData] {
  def execute[R](in: CreateAccountInputData): Eff[R, CreateAccountOutputData] = for {
    created <- Account.applyEff[R]
  } yield CreateAccountOutputData(payload = created)
}

case class CreateAccountInputData()                  extends InputData
case class CreateAccountOutputData(payload: Account) extends OutputData
