package adapter.eff

import domain.eff.MyErrorEither.MyErrorEither
import domain.eff._
import org.atnos.eff._

object Stack {
  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / アダプター層: エフェクトの追加
  type AStack = Fx.fx2[IdGen, MyErrorEither]
}
