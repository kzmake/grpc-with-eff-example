package adapter.secondary.map

import domain.eff.MyErrorEither._myErrorEither
import domain.eff._
import domain.error.{MyError, UnauthorizedError}
import org.atnos.eff.Interpret.translate
import org.atnos.eff._
import org.atnos.eff.either._
import org.atnos.eff.syntax.all._

import scala.collection.concurrent.TrieMap

object MapAuthzOps extends AuthzInterpreter {
  private val scopesStore = TrieMap.empty[String, Set[String]]
  private val policiesStore = TrieMap(
    "alice" -> Set("1"),
    "bob"   -> Set("2")
  )

  implicit class AuthzOps[R, A](effects: Eff[R, A]) {
    def runAuthz[U](principal: String)(implicit
        m: Member.Aux[Authz, R, U],
        m1: _myErrorEither[U]
    ): Eff[U, A] = run(effects << Authz.authorize(principal))

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
          case Allocate(principal, scopes) =>
            val p = policiesStore.getOrElse(principal, Set.empty)
            policiesStore += (principal -> (p ++ scopes))
            ().asInstanceOf[X].pureEff[U]

          case Require(scopes) =>
            val s = scopesStore.getOrElse("required", Set.empty)
            scopesStore += ("required" -> (s ++ scopes))
            ().asInstanceOf[X].pureEff[U]

          case Authorize(principal) =>
            for {
              _ <- {
                val scopes   = scopesStore.getOrElse("required", Set.empty)
                val policies = policiesStore.getOrElse(principal, Set.empty)
                if (scopes.forall(policies.contains))
                  fromEither[U, MyError, Unit](Right(()))
                else
                  fromEither[U, MyError, X](Left(UnauthorizedError(s"認可に失敗しました: $policies に $scopes が含まれていない")))
              }
              _ = scopesStore += ("required" -> Set.empty)
            } yield ().asInstanceOf[X]
        }
    })
  }
}
