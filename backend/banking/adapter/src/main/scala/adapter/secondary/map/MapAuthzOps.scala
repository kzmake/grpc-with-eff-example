package adapter.secondary.map

import domain.eff.MyErrorEither._myErrorEither
import domain.eff._
import domain.error._
import org.atnos.eff.Interpret.translate
import org.atnos.eff._
import org.atnos.eff.either._
import org.atnos.eff.syntax.all._

import scala.collection.concurrent.TrieMap

// TODO: 課題3: AuthZ(認可)エフェクトの実装 / アダプター層: インタープリターの実装
object MapAuthzOps extends AuthzInterpreter {
  // 今回は作り込まないでインメモリで認可するよ
  private val scopesStore = TrieMap.empty[String, Set[String]]
  private val policiesStore = TrieMap(
    "none"  -> Set.empty[String],
    "alice" -> Set("1", "11111111-1111-1111-1111-111111111111"),
    "bob"   -> Set("2")
  )

  implicit class AuthzOps[R, A](effects: Eff[R, A]) {
    // 最後にauthorizeを積んで実施: 認可したいAPIなどのコントローラーで利用
    def runAuthz[U](principal: String)(implicit
        m: Member.Aux[Authz, R, U],
        m1: _myErrorEither[U]
    ): Eff[U, A] = run(principal, effects << Authz.authorize)

    // authorizeなしで実施: バッチなどのコントローラーで利用
    def runAuthzWithoutAuthorize[U](implicit
        m: Member.Aux[Authz, R, U],
        m1: _myErrorEither[U]
    ): Eff[U, A] = run("system", effects)
  }

  def run[R, U, A](principal: String, effects: Eff[R, A])(implicit
      m: Member.Aux[Authz, R, U],
      m1: _myErrorEither[U]
  ): Eff[U, A] = ???
}
