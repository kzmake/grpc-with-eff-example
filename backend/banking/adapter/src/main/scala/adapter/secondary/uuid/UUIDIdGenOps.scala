package adapter.secondary.uuid

import java.util.UUID
import domain.shared.Id
import org.atnos.eff.Interpret.translate
import domain.eff._
import org.atnos.eff._
import org.atnos.eff.syntax.all._

object UUIDIdGenOps extends IdGenInterpreter {
  // TODO: 課題2: IdGen(Id生成)エフェクトのインタープリター実装 / アダプター層: uuidなID採番インタープリターの実装
  implicit class IdGenOps[R, A](effects: Eff[R, A]) {
    def runIdGen[U](implicit
        m: Member.Aux[IdGen, R, U]
    ): Eff[U, A] = ???
  }

  def run[R, U, A](generator: () => String, effects: Eff[R, A])(implicit
      m: Member.Aux[IdGen, R, U]
  ): Eff[U, A] = ???
}
