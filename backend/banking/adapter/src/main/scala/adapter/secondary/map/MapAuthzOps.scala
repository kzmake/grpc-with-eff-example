package adapter.secondary.map

import domain.eff.MyErrorEither._myErrorEither
import domain.eff._
import domain.error._
import org.atnos.eff.Interpret.translate
import org.atnos.eff._
import org.atnos.eff.either._
import org.atnos.eff.syntax.all._

import scala.collection.concurrent.TrieMap

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
    ): Eff[U, A] = run(effects << Authz.authorize(principal))

    // authorizeなしで実施: バッチなどのコントローラーで利用
    def runAuthzWithoutAuthorize[U](implicit
        m: Member.Aux[Authz, R, U],
        m1: _myErrorEither[U]
    ): Eff[U, A] = run(effects)
  }

  def run[R, U, A](effects: Eff[R, A])(implicit
      m: Member.Aux[Authz, R, U],
      m1: _myErrorEither[U]
  ): Eff[U, A] = {
    translate(effects)(new Translate[Authz, U] {
      def apply[X](a: Authz[X]): Eff[U, X] =
        a match {
          case Allocate(principal, s) =>
            val policies = policiesStore.getOrElse(principal, Set.empty)
            policiesStore += (principal -> (policies + s))
            ().asInstanceOf[X].pureEff[U]

          case Require(s) =>
            val scopes = scopesStore.getOrElse("required", Set.empty)
            scopesStore += ("required" -> (scopes + s))
            ().asInstanceOf[X].pureEff[U]

          case Authorize(principal) =>
            for {
              _ <- {
                val scopes   = scopesStore.getOrElse("required", Set.empty[String])
                val policies = policiesStore.getOrElse(principal, Set.empty[String])
                if (scopes.forall(s => policies.contains(s)))
                  fromEither[U, MyError, Unit](Right(()))
                else
                  fromEither[U, MyError, X](
                    Left(UnauthorizedError(s"認可に失敗しました: $principal の $policies に $scopes が含まれていない"))
                  )
              }
              _ = scopesStore += ("required" -> Set.empty)
            } yield ().asInstanceOf[X]
        }
    })
  }
}
