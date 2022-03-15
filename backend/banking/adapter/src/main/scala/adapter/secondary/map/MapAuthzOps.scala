package adapter.secondary.map

import domain.eff._
import org.atnos.eff.Interpret.translate
import org.atnos.eff._
import org.atnos.eff.syntax.all._

import scala.collection.concurrent.TrieMap

object MapAuthzOps extends AuthzInterpreter {
  val store = TrieMap(
    "required" -> List.empty[String],
    "alice"    -> List("1"),
    "bob"      -> List("2")
  )

  implicit class AuthzOps[R, A](effects: Eff[R, A]) {
    def runAuthz[U](principal: String)(implicit
        m: Member.Aux[Authz, R, U]
    ): Eff[U, A] = run(effects << Authz.authorize(principal))

    def runAuthzWithoutAuthorize[U](implicit
        m: Member.Aux[Authz, R, U]
    ): Eff[U, A] = run(effects)
  }

  def run[R, U, A](effects: Eff[R, A])(implicit
      m: Member.Aux[Authz, R, U]
  ): Eff[U, A] = {
    translate(effects)(new Translate[Authz, U] {
      def apply[X](a: Authz[X]): Eff[U, X] =
        a match {
          case Allocate(principal, scopes) =>
            store.addOne(principal, store(principal) ++ scopes)
            ().asInstanceOf[X].pureEff[U]

          case Require(scopes) =>
            store.update("required", store("required") ++ scopes)
            ().asInstanceOf[X].pureEff[U]

          case Authorize(principal) =>
            val scopes   = store("required")
            val policies = store(principal)
            if (scopes.forall(policies.contains(_))) println("OK")
            else println(s"KO : ${policies}, ${scopes}")

            ().asInstanceOf[X].pureEff[U]
        }
    })
  }
}
